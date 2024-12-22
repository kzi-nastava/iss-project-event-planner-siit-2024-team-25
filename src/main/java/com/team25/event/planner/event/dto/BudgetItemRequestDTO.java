package com.team25.event.planner.event.dto;

import lombok.Data;

@Data
public class BudgetItemRequestDTO {
    private Double budget;
    private Long offeringCategoryId;
    private Long eventId;
}
