package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.mapper.PurchaseMapper;
import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Money;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.event.specification.PurchaseSpecification;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ServiceRepository serviceRepository;
    private final PurchaseMapper purchaseMapper;
    private final EventRepository eventRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final PurchaseSpecification purchaseSpecification;


    // mapper
    public PurchasedProductResponseDTO purchaseProduct(Long eventId, Long productId, PurchaseProductRequestDTO object){
        //Optional<Product> productRepo = productRepository.findById(productId); // product exist?
        //Product product = productRepo.orElseThrow(() -> new RuntimeException("Product not found!"));


        boolean isProductSuitable = true;
//        boolean isProductSuitable = eventService.isProductSuitable(product.getPrice(),product.getOfferingCategory().getStatus(), eventId);
        if(!isProductSuitable){
            throw new IllegalArgumentException("Product is not suitable in the requested budget list");
        }

        PurchasedProductResponseDTO responseDTO = new PurchasedProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setProductId(productId);
        responseDTO.setEventId(eventId);
        responseDTO.setPrice(object.getPrice());
        responseDTO.setOfferingCategoryId(object.getOfferingCategoryId());

        //save to repo
        return responseDTO;

    }

    public PurchaseServiceResponseDTO purchaseService(PurchaseServiceRequestDTO requestDTO, Long eventId, Long serviceId) {
        boolean isServiceAvailable = isServiceAvailable(serviceId,requestDTO);
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new NotFoundError("Service not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        Purchase purchase = purchaseMapper.toPurchase(requestDTO, event, service);
        boolean isServiceSuitable = this.isOfferingSuitable(purchase.getPrice(),service.getOfferingCategory(), event);


        if(isServiceAvailable && isServiceSuitable){
            purchaseRepository.save(purchase);
            return purchaseMapper.toServiceResponseDTO(purchase);
        } else if (!isServiceAvailable) {
            throw new IllegalArgumentException("Service is not available in the specified period.");
        }
        else{
            throw new IllegalArgumentException("Service is not suitable in the requested budget list");
        }
    }


    public boolean isServiceAvailable(Long serviceId, PurchaseServiceRequestDTO requestDTO){
        Specification<Purchase> specification = purchaseSpecification.createServiceSpecification(requestDTO, serviceId);
        return !purchaseRepository.exists(specification);
    }

    public boolean isOfferingSuitable(Money servicePrice, OfferingCategory offeringCategory, Event event){
        Collection<BudgetItem> budgetItems = event.getBudgetItemCollection();
        for (BudgetItem budgetItem: budgetItems){
            if(budgetItem.getOfferingCategory().equals(offeringCategory)){
                double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event.getId(), offeringCategory.getId());
                return budgetItem.getMoney().getAmount() - totalSpent >= servicePrice.getAmount();
            }
        }
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setOfferingCategory(offeringCategory);
        budgetItem.setEvent(event);
        budgetItem.setMoney(servicePrice);
        budgetItemRepository.save(budgetItem);
        return true;
    }

    public Double getLeftMoneyFromBudgetItem(Long eventId, Long categoryId) {
        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(eventId, categoryId);
        BudgetItem budgetItem = budgetItemRepository.findBudgetItemByEventIdAndOfferingCategoryId(eventId, categoryId).orElseThrow(() -> new NotFoundError("Budget item not found"));
        return budgetItem.getMoney().getAmount()-totalSpent;
    }
}
