package com.team25.event.planner.offering.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfferingPreviewResponseDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String description;
    private String country;
    private String city;
    private double rating;
    private double price;
    private Boolean isService;
}
