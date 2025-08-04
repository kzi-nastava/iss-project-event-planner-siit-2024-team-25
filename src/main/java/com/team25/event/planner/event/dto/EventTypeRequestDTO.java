package com.team25.event.planner.event.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class EventTypeRequestDTO {
    @NotEmpty(message = "Name is required")
    private final String name;

    private final String description;

    private Boolean isActive;

    private List<Long> categories;
}
