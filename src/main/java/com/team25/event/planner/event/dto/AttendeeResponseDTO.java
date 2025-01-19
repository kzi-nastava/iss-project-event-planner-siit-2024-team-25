package com.team25.event.planner.event.dto;

import lombok.Data;

@Data
public class AttendeeResponseDTO {
    private final Long userId;
    private final String fullName;
}
