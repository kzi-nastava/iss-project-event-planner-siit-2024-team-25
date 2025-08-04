package com.team25.event.planner.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BudgetItemResponseDTO {
    private Long id;
    private Double budget;
    private Long offeringCategoryId;
    private Long eventId;
}
