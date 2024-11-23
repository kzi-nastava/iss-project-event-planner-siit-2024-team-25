package com.team25.event.planner.offering.common.dto;

import lombok.Data;

@Data
public class PriceListItemUpdateResponseDTO {
    private Long id;
    private double price;
    private double discount;
    private double priceWithDiscount;
}
