package com.team25.event.planner.event.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventPreviewResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalTime startTime;
}
