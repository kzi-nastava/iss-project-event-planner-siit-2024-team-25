package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.email.service.EmailService;
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
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final OfferingRepository offeringRepository;


    // mapper
    public PurchasedProductResponseDTO purchaseProduct(Long eventId, PurchaseProductRequestDTO dto){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new NotFoundError("Product not found"));
        Purchase purchase = purchaseMapper.toPurchase(event,product);
        if(!product.isAvailable()){
            throw new InvalidRequestError("Product is not available");
        }
        if(!isProductCategorySuitableForEvent(event,product.getOfferingCategory().getId())){
            throw new InvalidRequestError("Product category is not suitable for event");
        }
        Double leftMoney = getLeftMoneyFromBudgetItem(eventId,product.getOfferingCategory().getId());
        // does not exist budget item for offering category
        if(leftMoney==-1){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setOfferingCategory(product.getOfferingCategory());
            budgetItem.setEvent(event);
            budgetItem.setMoney(new Money(purchase.getPrice().getAmount()));
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

    private boolean isProductCategorySuitableForEvent(Event event, Long offeringCategoryId){
        if(event.getEventType()==null) return true;
        return event.getEventType().getOfferingCategories().stream().map(OfferingCategory::getId).anyMatch(offeringCategoryId::equals);
    }

    public PurchaseServiceResponseDTO purchaseService(PurchaseServiceRequestDTO requestDTO, Long eventId, Long serviceId) {
        boolean isServiceAvailable = isServiceAvailable(serviceId,requestDTO);
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new NotFoundError("Service not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        Purchase purchase = purchaseMapper.toPurchase(requestDTO, event, service);
        boolean isPurchaseRequestValid = this.isPurchaseRequestValid(purchase, event, service);

        if(!service.isAvailable()) {
            throw new InvalidRequestError("Service is not available.");
        }else if(!isServiceAvailable){
            throw new InvalidRequestError("Service is not available in this period.");
        }
        else if(!isProductCategorySuitableForEvent(event,service.getOfferingCategory().getId())){
            throw new InvalidRequestError("Product category is not suitable for event.");
        } else if (!isPurchaseRequestValid) {
            throw new InvalidRequestError("Purchase request has invalid data.");
        }

        Double leftMoney = getLeftMoneyFromBudgetItem(eventId, service.getOfferingCategory().getId());
        if(leftMoney==-1){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setOfferingCategory(service.getOfferingCategory());
            budgetItem.setEvent(event);
            budgetItem.setMoney(new Money(purchase.getPrice().getAmount()));
            budgetItemRepository.save(budgetItem);
            purchaseRepository.save(purchase);
            emailService.sendServicePurchaseConfirmation(purchase);
            return purchaseMapper.toServiceResponseDTO(purchase);
        }
        double servicePrice = service.getPrice() * (100-service.getDiscount()) / 100;
        if(servicePrice <= leftMoney){
            emailService.sendServicePurchaseConfirmation(purchase);
            return purchaseMapper.toServiceResponseDTO(purchaseRepository.save(purchase));
        }else{
            throw new InvalidRequestError("Not enough budget plan money for the service");
        }
    }

    private boolean isPurchaseRequestValid(Purchase purchase, Event event, com.team25.event.planner.offering.service.model.Service service) {
        if(event.getEndDate().isBefore(LocalDate.now())){
            return false;
        }

        LocalDateTime purchaseStartDateTime = LocalDateTime.of(purchase.getStartDate(), purchase.getStartTime());
        LocalDateTime purchaseRequest = LocalDateTime.now();
        long deadline = java.time.Duration.between(purchaseRequest, purchaseStartDateTime).toHours();

        boolean idDeadlineEnd = deadline >= service.getReservationDeadline();

        LocalDateTime purchaseEndDateTime = LocalDateTime.of(purchase.getEndDate(), purchase.getEndTime());

        LocalDateTime eventStartDateTime = LocalDateTime.of(event.getStartDate(), event.getStartTime());
        LocalDateTime eventEndDateTime = LocalDateTime.of(event.getEndDate(), event.getEndTime());

        boolean isTimeValid = !purchaseStartDateTime.isBefore(eventStartDateTime) &&
                !purchaseStartDateTime.isAfter(eventEndDateTime) &&
                !purchaseEndDateTime.isBefore(eventStartDateTime) &&
                !purchaseEndDateTime.isAfter(eventEndDateTime);

        long durationInMinutes = java.time.Duration.between(purchaseStartDateTime, purchaseEndDateTime).toMinutes();
        boolean isDurationValid;
        if(service.getDuration() > 0){
            isDurationValid= durationInMinutes == service.getDuration()*60;
        }else isDurationValid = durationInMinutes <= service.getMaximumArrangement()*60 && durationInMinutes >= service.getMinimumArrangement()*60;

        double servicePrice = service.getPrice() * (100-service.getDiscount()) / 100;
        boolean isPriceValid = purchase.getPrice().getAmount() == servicePrice;


        return isTimeValid && isDurationValid && idDeadlineEnd && isPriceValid;
    }



    public boolean isServiceAvailable(Long serviceId, PurchaseServiceRequestDTO requestDTO){
        Specification<Purchase> specification = purchaseSpecification.createServiceSpecification(requestDTO, serviceId);
        return !purchaseRepository.exists(specification);
    }

    public Double getLeftMoneyFromBudgetItem(Long eventId, Long categoryId) {
        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(eventId, categoryId);
        BudgetItem budgetItem = budgetItemRepository.findBudgetItemByEventIdAndOfferingCategoryId(eventId, categoryId).orElse(null);
        if(budgetItem == null){
            return -1.0;
        }
        return budgetItem.getMoney().getAmount()-totalSpent;
    }

    public List<Purchase> findOwnerPurchasesOverlappingDateRange(Long ownerId, LocalDate startDate, LocalDate endDate) {
        return purchaseRepository.findByOfferingOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(ownerId, endDate, startDate);
    }

    public List<PurchaseServicePreviewResponseDTO> getOwnerPurchasesOverlappingDateRange(Long ownerId, LocalDate startDate, LocalDate endDate) {
        return findOwnerPurchasesOverlappingDateRange(ownerId, startDate, endDate)
                .stream().map(purchaseMapper::toPurchaseServicePreviewResponseDTO).toList();
    }

    public boolean eventOrganizerHasFuturePurchases(Long organizerId) {
        return purchaseRepository.existsByEventOrganizerIdAndStartDateGreaterThanEqual(organizerId, LocalDate.now());
    }

    public boolean ownerHasFuturePurchases(Long ownerId) {
        return purchaseRepository.existsByOfferingOwnerIdAndStartDateGreaterThanEqual(ownerId, LocalDate.now());
    }
    public List<PurchasePreviewResponseDTO> getEventsPurchase(Long eventId){
        Event e = eventRepository.findById(eventId).orElseThrow(()->new NotFoundError("Event not found"));
        return purchaseRepository.findAllByEvent(e).stream().map(purchaseMapper::toPurchasePreviewResponseDTO).toList();
    }
    public List<PurchasePreviewResponseDTO> getOfferingsPurchase(Long offeringId){
        Offering o = offeringRepository.findById(offeringId).orElseThrow(()->new NotFoundError("Offering not found"));
        return purchaseRepository.findAllByOffering(o).stream().map(purchaseMapper::toPurchasePreviewResponseDTO).toList();
    }
}
