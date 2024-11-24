package com.team25.event.planner.offering.common.mapper;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferingMapper {
    OfferingPreviewResponseDTO toDTO(Offering offering);
}
