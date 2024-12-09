package com.team25.event.planner.offering.common.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class OfferingFilterDTO {
    private String name;
    private Long eventTypeId;
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private Boolean isAvailable;
    private String description;
}
