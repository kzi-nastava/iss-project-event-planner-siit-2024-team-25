package com.team25.event.planner.offering.common.dto;

import com.team25.event.planner.offering.common.model.OfferingCategory;

import java.time.LocalDate;

public class PurchaseProductRequestDTO {
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private double discount;
    private Long offeringCategoryId; // adding price to selected products category so that system can calculate budget for this category
}
