package com.team25.event.planner.event.dto;

import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.event.model.PrivacyType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventRequestDTO {
    private Long eventTypeId;

    @NotEmpty(message = "Name is required")
    private String name;

    private String description;

    private Integer maxParticipants;

    @NotNull(message = "Privacy type is required")
    private PrivacyType privacyType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Location is required")
    private LocationRequestDTO location;
}
