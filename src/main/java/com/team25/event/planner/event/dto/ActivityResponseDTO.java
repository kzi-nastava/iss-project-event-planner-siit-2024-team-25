package com.team25.event.planner.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
}
