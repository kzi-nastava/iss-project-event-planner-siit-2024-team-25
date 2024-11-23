package com.team25.event.planner.offering.service.dto;

import lombok.Data;

@Data
public class ServiceCardResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
}
