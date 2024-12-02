package com.team25.event.planner.offering.common.mapper;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferingMapper {

    @Mapping(target = "ownerName", source = "offering.owner.firstName")
    @Mapping(target = "country", source = "offering.owner.companyAddress.country")
    @Mapping(target = "city", source = "offering.owner.companyAddress.city")
    OfferingPreviewResponseDTO toDTO(Offering offering);
}
