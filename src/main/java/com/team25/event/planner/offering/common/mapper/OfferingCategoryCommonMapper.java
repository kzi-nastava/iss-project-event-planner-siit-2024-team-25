package com.team25.event.planner.offering.common.mapper;

import com.team25.event.planner.offering.common.dto.OfferingCategoryCreateRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferingCategoryCommonMapper {
    OfferingCategory toOfferingCategory(OfferingCategoryCreateRequestDTO offeringCategoryRequestDTO);

    @Mapping(source = "status", target = "status")
    OfferingCategoryResponseDTO toResponseDTO(OfferingCategory offeringCategory);

    default OfferingCategoryType mapRole(OfferingCategoryType type) {
        switch (type) {
            case ACCEPTED:
                return OfferingCategoryType.ACCEPTED;
            case PENDING:
                return OfferingCategoryType.PENDING;
            default:
                throw new IllegalArgumentException("Unexpected type: " + type);
        }
    }
}
