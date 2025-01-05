package com.team25.event.planner.user.dto;

import com.team25.event.planner.user.model.UserRole;
import lombok.Data;

import java.time.Instant;

@Data
public class LoginResponseDTO {
    private final Long userId;
    private final String email;
    private final String fullName;
    private final UserRole role;
    private final String jwt;
    private final Instant suspensionEndDateTime;
}
