package com.team25.event.planner.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SuspensionResponseDTO {
    private Long id;
    private Long suspendedUserId;
    private String suspendedUserFirstName;
    private String suspendedUserLastName;
    private Instant expirationTime;
}
