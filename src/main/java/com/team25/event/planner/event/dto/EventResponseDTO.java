package com.team25.event.planner.event.dto;

import com.team25.event.planner.event.model.PrivacyType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventResponseDTO {
    private Long id;
    private EventTypePreviewResponseDTO eventType;
    private String name;
    private String description;
    private Integer maxParticipants;
    private PrivacyType privacyType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
