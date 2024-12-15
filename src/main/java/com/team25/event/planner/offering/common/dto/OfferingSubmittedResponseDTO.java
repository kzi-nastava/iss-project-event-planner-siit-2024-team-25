package com.team25.event.planner.offering.common.dto;

import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfferingSubmittedResponseDTO {
    private Long offeringId;
    private String offeringName;
    private Long categoryId;
    private String categoryName;
    private String description;
    private OfferingCategoryType status;
}
