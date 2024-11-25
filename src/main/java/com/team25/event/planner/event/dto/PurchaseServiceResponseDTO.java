package com.team25.event.planner.event.dto;

import com.team25.event.planner.event.model.Money;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PurchaseServiceResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private Money price;
    private Long offeringCategoryId;
    private Long eventId;
    private Long serviceId;
}
