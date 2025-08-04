package com.team25.event.planner.event.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchasedProductResponseDTO {
    private Long id;
    private double price;
    private Long offeringCategoryId;
    private Long eventId;
    private Long productId;
}
