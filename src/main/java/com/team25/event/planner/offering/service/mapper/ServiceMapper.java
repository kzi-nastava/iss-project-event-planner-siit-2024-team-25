package com.team25.event.planner.offering.service.mapper;


import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.service.dto.ServiceCreateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceCreateResponseDTO;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ServiceMapperHelper.class})
public interface ServiceMapper {
    @Mapping(source = "visible", target = "visible")
    @Mapping(source = "available", target = "available")
    @Mapping(source = "eventTypes", target = "eventTypesIDs")
    @Mapping(source = "owner", target = "ownerID")
    @Mapping(source = "offeringCategory", target = "offeringCategoryID")
    ServiceCreateResponseDTO toDTO(Service service);

    @Mapping(source = "dto.available", target = "available")
    @Mapping(source = "dto.visible", target = "visible")
    @Mapping(target = "status", ignore = true)
    Service toEntity(ServiceCreateRequestDTO dto);

    default Long mapOwnerToId(Owner owner) {
        return owner != null ? owner.getId() : null;
    }

    default Long mapOfferingCategoryToId(OfferingCategory category) {
        return category != null ? category.getId() : null;
    }

    default Long mapEventTypeToId(EventType eventType) {
        return eventType != null ? eventType.getId() : null;
    }

    default List<Long> mapEventTypesToIds(List<EventType> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::mapEventTypeToId).toList();
    }

}
