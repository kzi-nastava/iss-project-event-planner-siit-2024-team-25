package com.team25.event.planner.event.dto;

import com.team25.event.planner.event.model.PrivacyType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventFilterDTO {
    private final String nameContains;
    private final String descriptionContains;
    private final Long eventTypeId;
    private final PrivacyType privacyType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer maxParticipants;
    private final String country;
    private final String city;
}
