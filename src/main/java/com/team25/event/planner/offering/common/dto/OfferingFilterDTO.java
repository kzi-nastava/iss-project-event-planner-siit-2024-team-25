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
    private String country;
    private String city;
    private Date startDate;
    private Date endDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
