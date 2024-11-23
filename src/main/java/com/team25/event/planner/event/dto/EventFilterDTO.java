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
    private final LocalDate startDateFrom;
    private final LocalDate startDateTo;
    private final LocalDate endDateFrom;
    private final LocalDate endDateTo;
    private final LocalTime startTimeFrom;
    private final LocalTime startTimeTo;
    private final LocalTime endTimeFrom;
    private final LocalTime endTimeTo;
    private final Integer minParticipants;
    private final Integer maxParticipants;
    private final String city;
}
