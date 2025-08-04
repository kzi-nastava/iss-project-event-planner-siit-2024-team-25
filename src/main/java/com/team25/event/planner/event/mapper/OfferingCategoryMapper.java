package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.OfferingCategoryPreviewDTO;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferingCategoryMapper {
    OfferingCategoryPreviewDTO toDTO(OfferingCategory offeringCategory);
}
