package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.communication.model.NotificationCategory;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.mapper.PurchaseMapper;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.event.specification.PurchaseSpecification;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ServiceRepository serviceRepository;
    private final PurchaseMapper purchaseMapper;
    private final EventRepository eventRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final PurchaseSpecification purchaseSpecification;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;


    // mapper
    public PurchasedProductResponseDTO purchaseProduct(Long eventId, PurchaseProductRequestDTO dto){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new NotFoundError("Product not found"));
        Purchase purchase = purchaseMapper.toPurchase(event,product);
        purchase.getPrice().setCurrency("EUR");
        if(!product.isAvailable()){
            throw new InvalidRequestError("Product is not available");
        }
        if(!isProductCategorySuitableForEvent(event,product)){
            throw new InvalidRequestError("Product category is not suitable for event");
        }
        Double leftMoney = getLeftMoneyFromBudgetItem(eventId,product.getOfferingCategory().getId());
        // does not exist budget item for offering category
        if(leftMoney==-1){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setOfferingCategory(product.getOfferingCategory());
            budgetItem.setEvent(event);
            budgetItem.setMoney(new Money(purchase.getPrice().getAmount(), "EUR"));
            budgetItemRepository.save(budgetItem);
            return purchaseMapper.toProductResponseDTO(purchaseRepository.save(purchase));
        }
        // exists and have enough money
        if(product.getPrice() <= leftMoney){
            return purchaseMapper.toProductResponseDTO(purchaseRepository.save(purchase));
        }else{
            throw new InvalidRequestError("Not enough budget plan money for the product");
        }

    }

    private boolean isProductCategorySuitableForEvent(Event event, Product product){
        return event.getEventType().getOfferingCategories().stream().map(OfferingCategory::getId).anyMatch(product.getOfferingCategory().getId()::equals);
    }

    public PurchaseServiceResponseDTO purchaseService(PurchaseServiceRequestDTO requestDTO, Long eventId, Long serviceId) {
        boolean isServiceAvailable = isServiceAvailable(serviceId,requestDTO);
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new NotFoundError("Service not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        notificationService.sendNotification("Event", "Dear, you receive this notification!", eventId, NotificationCategory.EVENT, event.getOrganizer());
        Purchase purchase = purchaseMapper.toPurchase(requestDTO, event, service);
        purchase.getPrice().setCurrency("$");
        boolean isPurchaseRequestValid = this.isPurchaseRequestValid(purchase, event);
        boolean isServiceSuitable = this.isOfferingSuitable(purchase.getPrice(),service.getOfferingCategory(), event);

        if(isServiceAvailable && isServiceSuitable && isPurchaseRequestValid){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setOfferingCategory(service.getOfferingCategory());
            budgetItem.setEvent(event);
            budgetItem.setMoney(purchase.getPrice());
            budgetItemRepository.save(budgetItem);
            purchaseRepository.save(purchase);

            return purchaseMapper.toServiceResponseDTO(purchase);
        } else if (!isServiceAvailable) {
            throw new InvalidRequestError("Service is not available in the specified period.");
        }
        else if(!isServiceSuitable){
            throw new InvalidRequestError("Service is not suitable in the requested budget list");
        }else{
            throw new InvalidRequestError("Purchase request has invalid date and time");
        }
    }

    private boolean isPurchaseRequestValid(Purchase purchase, Event event) {
        return event.getStartDate() == purchase.getStartDate()
                && event.getEndDate() == purchase.getEndDate()
                && event.getStartTime() == purchase.getStartTime()
                && event.getEndTime() == purchase.getEndTime();
    }


    public boolean isServiceAvailable(Long serviceId, PurchaseServiceRequestDTO requestDTO){
        Specification<Purchase> specification = purchaseSpecification.createServiceSpecification(requestDTO, serviceId);
        return !purchaseRepository.exists(specification);
    }

    public boolean isOfferingSuitable(Money offeringPrice, OfferingCategory offeringCategory, Event event){
        Collection<BudgetItem> budgetItems = event.getBudgetItemCollection();
        for (BudgetItem budgetItem: budgetItems){
            if(budgetItem.getOfferingCategory().equals(offeringCategory)){
                double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event.getId(), offeringCategory.getId());
                return budgetItem.getMoney().getAmount() - totalSpent >= offeringPrice.getAmount();
            }
        }
        return true;
    }

    public Double getLeftMoneyFromBudgetItem(Long eventId, Long categoryId) {
        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(eventId, categoryId);
        BudgetItem budgetItem = budgetItemRepository.findBudgetItemByEventIdAndOfferingCategoryId(eventId, categoryId).orElse(null);
        if(budgetItem == null){
            return -1.0;
        }
        return budgetItem.getMoney().getAmount()-totalSpent;
    }
}
