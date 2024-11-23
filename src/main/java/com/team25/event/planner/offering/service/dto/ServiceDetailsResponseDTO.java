package com.team25.event.planner.offering.service.dto;

import com.team25.event.planner.offering.common.dto.OfferingCategoryServiceResponseDTO;
import com.team25.event.planner.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.offering.service.model.ReservationType;
import lombok.Data;

import java.util.List;

@Data
public class ServiceDetailsResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> images;
    private boolean isAvailable;
    private List<EventTypeServiceResponseDTO> eventTypes;
    private OfferingCategoryServiceResponseDTO offeringCategory;
    private ReservationType reservationType;
    private String specifics;
    private int duration;
    private int reservationDeadline;
    private int cancellationDeadline;
}
