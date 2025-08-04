package com.team25.event.planner.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetItemRequestDTO {
    private Double budget;
    private Long offeringCategoryId;
    private Long eventId;
}
