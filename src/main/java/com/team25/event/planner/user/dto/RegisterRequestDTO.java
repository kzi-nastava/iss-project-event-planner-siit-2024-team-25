package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.util.ValidationPatterns;
import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.validation.AllowedRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO extends UserRequestDTO {
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = ValidationPatterns.EMAIL_REGEX,
            message = "Email must be valid"
    )
    private String email;

    @NotEmpty(message = "Password is required")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_REGEX,
            message = "Password must contain at least 8 characters, at least one letter and one number"
    )
    private String password;

    @AllowedRoles(
            value = {UserRole.EVENT_ORGANIZER, UserRole.OWNER},
            message = "Only EVENT_ORGANIZER and OWNER roles are allowed"
    )
    @Override
    public UserRole getUserRole() {
        return super.getUserRole();
    }
}
