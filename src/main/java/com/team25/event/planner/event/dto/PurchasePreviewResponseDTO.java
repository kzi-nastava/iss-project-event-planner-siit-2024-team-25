package com.team25.event.planner.event.dto;

import com.team25.event.planner.event.model.Money;
import com.team25.event.planner.offering.common.dto.OfferingCategoryPreviewResponseDTO;
import lombok.Data;

@Data
public class PurchasePreviewResponseDTO {
    private Long id;
    private OfferingCategoryPreviewResponseDTO offering;
    private Money price;
    private EventPreviewResponseDTO event;
}
