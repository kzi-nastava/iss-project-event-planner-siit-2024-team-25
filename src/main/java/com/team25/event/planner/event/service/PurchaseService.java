package com.team25.event.planner.event.service;

import com.team25.event.planner.event.dto.PurchaseProductRequestDTO;
import com.team25.event.planner.event.dto.PurchasedProductResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.product.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final EventService eventService;

    // mapper
    public PurchasedProductResponseDTO purchaseProduct(Long eventId, Long productId, PurchaseProductRequestDTO object){
        //Optional<Product> productRepo = productRepository.findById(productId); // product exist?
        //Product product = productRepo.orElseThrow(() -> new RuntimeException("Product not found!"));


        boolean isProductSuitable = true;
        //boolean isProductSuitable = eventService.isProductSuitable(product.getPrice(),product.getOfferingCategory().getStatus(), eventId);
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
}
