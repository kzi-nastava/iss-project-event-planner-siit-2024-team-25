package com.team25.event.planner.event.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchaseProductRequestDTO {
    private double price;
    private Long offeringCategoryId; // adding price to selected products category so that system can calculate budget for this category
}
