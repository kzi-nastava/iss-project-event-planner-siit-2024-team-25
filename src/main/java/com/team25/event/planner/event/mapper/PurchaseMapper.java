package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.PurchaseProductRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceResponseDTO;
import com.team25.event.planner.event.dto.PurchasedProductResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mapping(target = "offeringCategoryId", source = "purchase.offering.offeringCategory.id")
    @Mapping(target = "eventId", source = "purchase.event.id")
    @Mapping(target = "serviceId", source = "purchase.offering.id")
    PurchaseServiceResponseDTO toServiceResponseDTO(Purchase purchase);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price.amount", source = "purchaseServiceRequestDTO.price")
    @Mapping(target = "startDate", source = "purchaseServiceRequestDTO.startDate")
    @Mapping(target = "endDate", source = "purchaseServiceRequestDTO.endDate")
    @Mapping(target = "startTime", source = "purchaseServiceRequestDTO.startTime")
    @Mapping(target = "endTime", source = "purchaseServiceRequestDTO.endTime")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "offering", source = "service")
    Purchase toPurchase(PurchaseServiceRequestDTO purchaseServiceRequestDTO, Event event, Service service);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price.amount", source = "product.price")
    @Mapping(target = "startDate", source = "event.startDate")
    @Mapping(target = "endDate", source = "event.endDate")
    @Mapping(target = "startTime", source = "event.startTime")
    @Mapping(target = "endTime", source = "event.endTime")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "offering", source = "product")
    Purchase toPurchase(Event event, Product product);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "price", source = "price.amount")
    @Mapping(target = "offeringCategoryId", source = "offering.offeringCategory.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "productId", source = "offering.id")
    PurchasedProductResponseDTO toProductResponseDTO(Purchase purchase);
}
