package com.team25.event.planner.communication.dto;

import lombok.Data;

@Data
public class UserToOrganizerRequestDTO {
    private Long senderId;
    private Long organizerId;
    private Long eventId;
}
