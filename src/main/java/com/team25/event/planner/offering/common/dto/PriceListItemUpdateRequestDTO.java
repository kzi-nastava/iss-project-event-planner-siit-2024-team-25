package com.team25.event.planner.offering.common.dto;

import lombok.Data;

@Data
public class PriceListItemUpdateRequestDTO {
    private double price;
    private double discount;
}
