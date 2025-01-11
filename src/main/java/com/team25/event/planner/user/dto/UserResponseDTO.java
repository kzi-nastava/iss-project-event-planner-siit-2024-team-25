package com.team25.event.planner.user.dto;

import com.team25.event.planner.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private UserRole userRole;
}
