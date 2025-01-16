package com.team25.event.planner.user.dto;

import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.validation.ValidRoleBasedRegistration;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@ValidRoleBasedRegistration
public class UserRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private MultipartFile profilePicture;

    private Boolean removeProfilePicture;

    @NotNull(message = "User role is required")
    private UserRole userRole;

    @Nullable
    @Valid
    private EventOrganizerRequestDTO eventOrganizerFields;

    @Nullable
    @Valid
    private OwnerRequestDTO ownerFields;
}
