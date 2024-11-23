package com.team25.event.planner.event.dto;

import lombok.Data;

@Data
public class EventTypeResponseDTO {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean isActive;
}
