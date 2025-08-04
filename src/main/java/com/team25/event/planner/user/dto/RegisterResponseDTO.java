package com.team25.event.planner.user.dto;

import com.team25.event.planner.user.model.UserRole;
import lombok.Data;

@Data
public class RegisterResponseDTO {
    private final String email;
    private final String fullName;
    private final UserRole userRole;
}
