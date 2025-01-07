package com.team25.event.planner.offering.service.mapper;


import com.team25.event.planner.event.dto.EventTypePreviewResponseDTO;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.offering.common.dto.OfferingCategoryPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OwnerPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.service.dto.*;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.dto.OwnerResponseDTO;
import com.team25.event.planner.user.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ServiceMapperHelper.class})
public interface ServiceMapper {

    @Mapping(source = "visible", target = "visible")
    @Mapping(source = "available", target = "available")
    @Mapping(source = "eventTypes", target = "eventTypes")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "offeringCategory", target = "offeringCategory")
    ServiceCreateResponseDTO toDTO(Service service);

    @Mapping(source = "visible", target = "visible")
    @Mapping(source = "available", target = "available")
    @Mapping(source = "eventTypes", target = "eventTypesIDs")
    ServiceUpdateResponseDTO toUpdateDTO(Service service);

    @Mapping(source = "visible", target = "visible")
    @Mapping(source = "available", target = "available")
    Service toUpdatedService(ServiceUpdateResponseDTO serviceUpdateResponseDTO);

    @Mapping(source = "available", target = "available")
    @Mapping(source = "visible", target = "visible")
    @Mapping(target = "status", ignore = true)
    Service toEntity(ServiceCreateRequestDTO dto);

    default OfferingCategoryPreviewResponseDTO mapOfferingCategoryToDTO(OfferingCategory off){
        return new OfferingCategoryPreviewResponseDTO(off.getId(), off.getName());
    }
    default OwnerPreviewResponseDTO mapOwnerToId(Owner owner) {
        return new OwnerPreviewResponseDTO(owner.getId(), owner.getFirstName() +" " + owner.getLastName());
    }

    default Long mapEventTypeToId(EventType eventType) {
        return eventType != null ? eventType.getId() : null;
    }

    default List<EventTypePreviewResponseDTO> mapEventTypesToIds(List<EventType> entities) {
        if (entities == null) {
            return null;
        }
        List<EventTypePreviewResponseDTO> res = new ArrayList<>();
        for(EventType entity : entities) {
            res.add(new EventTypePreviewResponseDTO(entity.getId(), entity.getName()));
        }
        return res;
    }

    @Mapping(source = "images", target = "image")
    ServiceCardResponseDTO toCardDTO(Service service);

    default String mapFirstImage(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.getFirst();
    }

}
