package com.team25.event.planner.user.dto;

import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.model.UserStatus;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private UserRole userRole;
    private UserStatus userStatus;
}
