package com.team25.event.planner.offering.common.dto;

import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import lombok.Data;

@Data
public class OfferingCategoryCreateRequestDTO {
    private String name;
    private String description;
    private OfferingCategoryType type;
}
