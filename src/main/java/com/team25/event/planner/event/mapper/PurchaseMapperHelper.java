package com.team25.event.planner.event.mapper;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.PurchasePreviewResponseDTO;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.common.dto.OfferingCategoryPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingCategoryCommonMapper;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PurchaseMapperHelper {
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final OfferingCategoryCommonMapper offeringCategoryCommonMapper;

    @AfterMapping
    public void toPurchaseResponse(
            @MappingTarget Purchase purchase,
            PurchasePreviewResponseDTO previewResponseDTO
    ){
        previewResponseDTO.setOffering(toOfferingCategoryPreviewResponseDTO(purchase.getOffering().getId()));
    }

    private OfferingCategoryPreviewResponseDTO toOfferingCategoryPreviewResponseDTO(Long id){
        return offeringCategoryRepository.findById(id).map(offeringCategoryCommonMapper::toPreviewResponseDTO).orElseThrow(()->new NotFoundError("Offering category not found"));
    }
}
