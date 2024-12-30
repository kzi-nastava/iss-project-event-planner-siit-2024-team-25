package com.team25.event.planner.offering.common.dto;

import lombok.Data;

import java.security.PrivateKey;

@Data
public class PriceListItemResponseDTO {
    private Long offeringId;
    private String name;
    private double price;
    private double discount;
    private double priceWithDiscount;
}
