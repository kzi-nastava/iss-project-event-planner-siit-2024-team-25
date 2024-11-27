package com.team25.event.planner.offering.product.dto;

import com.team25.event.planner.event.dto.EventTypePreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryServiceResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private List<String> images;
    private boolean isVisible;
    private boolean isAvailable;
    private List<EventTypePreviewResponseDTO> eventTypes;
    private OfferingCategoryServiceResponseDTO offeringCategory;
}