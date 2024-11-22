package com.team25.event.planner.offering.service.dto;

import com.team25.event.planner.offering.common.dto.OfferingCategoryServiceResponseDTO;
import com.team25.event.planner.offering.event.dto.EventTypeServiceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
