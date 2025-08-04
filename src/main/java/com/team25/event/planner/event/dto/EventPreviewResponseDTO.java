package com.team25.event.planner.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventPreviewResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private String city;
    private String country;
    private String organizerFirstName;
    private String organizerLastName;
    private Boolean isFavorite;
}
