package com.team25.event.planner.offering.service.dto;

import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.service.model.ReservationType;

import java.util.List;

public class ServiceUpdateRequestDTO {
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> images;
    private boolean isActive;
    private boolean isAvailable;
    private String specifics;
    private OfferingType status;
    private ReservationType reservationType;
    private int duration;
    private int reservationDeadline;
    private int cancellationDeadline;
    private List<Long> eventTypesIDs;
}
