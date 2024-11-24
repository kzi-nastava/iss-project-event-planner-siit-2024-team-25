package com.team25.event.planner.offering.common.dto;

import lombok.Data;

@Data
public class OfferingPreviewResponseDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String description;
    private String country;
    private String city;
    private double price;
}
