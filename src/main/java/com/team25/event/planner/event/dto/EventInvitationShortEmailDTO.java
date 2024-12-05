package com.team25.event.planner.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class EventInvitationShortEmailDTO {
    private String eventName;
    private String eventDescription;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String eventCountry;
    private String eventCity;
    private String  eventAddress;
    private String eventInvitationCode;
}
