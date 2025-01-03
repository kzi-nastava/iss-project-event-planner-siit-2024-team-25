package com.team25.event.planner.offering.product.dto;

import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.user.dto.UserResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProductDetailsResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> images;
    private boolean isAvailable;
    private List<EventTypeServiceResponseDTO> eventTypes;
    private OfferingCategoryResponseDTO offeringCategory;
    private UserResponseDTO ownerInfo;
}
