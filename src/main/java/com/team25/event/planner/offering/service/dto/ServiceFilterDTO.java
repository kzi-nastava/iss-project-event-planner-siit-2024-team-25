package com.team25.event.planner.offering.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceFilterDTO {
    private final String name;
    private final Long eventTypeId;
    private final Long offeringCategoryId;
    private final Double price;
    private final Boolean available;
    private final Long ownerId;
}
