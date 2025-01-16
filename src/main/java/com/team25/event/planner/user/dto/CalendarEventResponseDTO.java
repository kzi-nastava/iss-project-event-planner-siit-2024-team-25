package com.team25.event.planner.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarEventResponseDTO {
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final EventType eventType;

    public enum EventType {
        EVENT,
        MY_EVENT,
        RESERVATION
    }
}
