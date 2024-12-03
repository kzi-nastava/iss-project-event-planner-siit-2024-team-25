package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.PurchaseProductRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceResponseDTO;
import com.team25.event.planner.event.dto.PurchasedProductResponseDTO;
import com.team25.event.planner.event.mapper.PurchaseMapper;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.product.service.ProductService;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final EventService eventService;
    private final ServiceRepository serviceRepository;
    private final PurchaseMapper purchaseMapper;
    private final EventRepository eventRepository;

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
//        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new NotFoundError("Service not found"));
//        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
//        boolean isServiceSuitable = eventService.isProductSuitable(purchase.getPrice(),service.getOfferingCategory().getStatus(), eventId);
//        boolean isServiceAvailable = isServiceAvailable(purchase);

        //test data
        Event event = new Event();
        event.setId(eventId);
        com.team25.event.planner.offering.service.model.Service service = new com.team25.event.planner.offering.service.model.Service();
        service.setId(serviceId);
        OfferingCategory offeringCategory = new OfferingCategory();
        offeringCategory.setId(requestDTO.getOfferingCategoryId());
        service.setOfferingCategory(offeringCategory);
        Purchase purchase = purchaseMapper.toPurchase(requestDTO, event, service);
        purchase.setId(1L);

        boolean isServiceSuitable = true;
        boolean isServiceAvailable = true;
        if(isServiceAvailable && isServiceSuitable){
            //purchase = purchaseRepository.save(purchase);
            return purchaseMapper.toServiceResponseDTO(purchase);
        } else if (!isServiceAvailable) {
            throw new IllegalArgumentException("Service is not available in the specified period.");
        }
        else{
            throw new IllegalArgumentException("Service is not suitable in the requested budget list");
        }
    }


    private boolean isServiceAvailable(Purchase purchase){
        return purchaseRepository.isAvailable(purchase.getOffering().getId(),purchase.getStartDate(), purchase.getStartTime(), purchase.getEndDate(), purchase.getEndTime());
    }
}
