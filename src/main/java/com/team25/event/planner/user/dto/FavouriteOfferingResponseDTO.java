package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class FavouriteOfferingResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
}
