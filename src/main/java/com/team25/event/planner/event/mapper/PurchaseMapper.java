package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.service.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mapping(target = "offeringCategoryId", source = "purchase.service.offeringCategory.id")
    @Mapping(target = "eventId", source = "purchase.event.id")
    @Mapping(target = "serviceId", source = "purchase.service.id")
    PurchaseServiceResponseDTO toServiceResponseDTO(Purchase purchase);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "purchaseServiceRequestDTO.price")
    @Mapping(target = "startDate", source = "purchaseServiceRequestDTO.startDate")
    @Mapping(target = "endDate", source = "purchaseServiceRequestDTO.endDate")
    @Mapping(target = "startTime", source = "purchaseServiceRequestDTO.startTime")
    @Mapping(target = "endTime", source = "purchaseServiceRequestDTO.endTime")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "service", source = "service")
    Purchase toPurchase(PurchaseServiceRequestDTO purchaseServiceRequestDTO, Event event, Service service);
}
