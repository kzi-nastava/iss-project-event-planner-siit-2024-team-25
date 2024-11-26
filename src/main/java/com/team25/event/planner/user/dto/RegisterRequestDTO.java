package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.util.ValidationPatterns;
import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.validation.ValidRoleBasedRegistration;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@ValidRoleBasedRegistration
public class RegisterRequestDTO {
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

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private MultipartFile profilePicture;

    @NotNull(message = "User role is required")
    private UserRole userRole;

    @Nullable
    @Valid
    private EventOrganizerRequestDTO eventOrganizerFields;

    @Nullable
    @Valid
    private OwnerRequestDTO ownerFields;
}
