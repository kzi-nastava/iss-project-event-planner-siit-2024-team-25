package com.team25.event.planner.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventPreviewResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private String city;
    private String country;
    private String organizerName;
    private Boolean isFavorite;
}
