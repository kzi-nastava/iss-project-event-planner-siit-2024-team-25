package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.util.ValidationPatterns;
import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.validation.AllowedRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class QuickRegisterRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private MultipartFile profilePicture;

    @NotNull(message = "User role is required")
    private UserRole userRole;
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
            value = {UserRole.REGULAR},
            message = "Only REGULAR role is allowed"
    )
    public UserRole getUserRole() {
        return this.userRole;
    }

}
