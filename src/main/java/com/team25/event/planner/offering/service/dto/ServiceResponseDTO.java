package com.team25.event.planner.offering.service.dto;

import com.team25.event.planner.offering.common.dto.OfferingCategoryServiceResponseDTO;
import com.team25.event.planner.offering.event.dto.EventTypeServiceResponseDTO;

import java.util.List;

public class ServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> images;
    private List<EventTypeServiceResponseDTO> eventTypes;
    private OfferingCategoryServiceResponseDTO offeringCategory;
}
