package com.team25.event.planner.offering.service.dto;

import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.service.model.ReservationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ServiceCreateResponseDTO {
    private Long Id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> images;
    private boolean visible;
    private boolean available;
    private String specifics;
    private OfferingType status;
    private ReservationType reservationType;
    private int duration;
    private int reservationDeadline;
    private int cancellationDeadline;
    private int minimumArrangement;
    private int maximumArrangement;
    private List<Long> eventTypesIDs;
    private Long OfferingCategoryID;
    private Long ownerID;
}
