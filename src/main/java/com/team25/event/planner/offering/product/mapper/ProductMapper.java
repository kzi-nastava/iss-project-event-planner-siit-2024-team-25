package com.team25.event.planner.offering.product.mapper;

import com.team25.event.planner.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.offering.common.dto.OwnerPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingCategoryCommonMapper;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.dto.ProductRequestDTO;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.user.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OfferingCategoryCommonMapper.class, ProductMapperHelper.class})
public interface ProductMapper {
    @Mapping(target = "ownerInfo", source = "owner")
    ProductResponseDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "status", source = "offeringStatus")
    @Mapping(target = "visible", defaultValue = "true")
    @Mapping(target = "available", defaultValue = "true")
    @Mapping(target = "deleted", constant = "false")
    Product toProduct(ProductRequestDTO dto, OfferingType offeringStatus, List<EventType> eventTypes, OfferingCategory offeringCategory, Owner owner);

    default OwnerPreviewResponseDTO mapOwner(Owner owner) {
        if (owner == null) {
            return null;
        }
        return new OwnerPreviewResponseDTO(owner.getId(), owner.getFullName());
    }

    default EventTypeServiceResponseDTO mapEventType(com.team25.event.planner.event.model.EventType eventType) {
        if (eventType == null) {
            return null;
        }
        return new EventTypeServiceResponseDTO(eventType.getId(), eventType.getName());
    }
}
