package com.team25.event.planner.offering.common.mapper;

import com.team25.event.planner.offering.common.dto.OfferingCategoryCreateRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferingCategoryCommonMapper {
    OfferingCategory toOfferingCategory(OfferingCategoryCreateRequestDTO offeringCategoryRequestDTO);

    OfferingCategoryResponseDTO toResponseDTO(OfferingCategory offeringCategory);
}
