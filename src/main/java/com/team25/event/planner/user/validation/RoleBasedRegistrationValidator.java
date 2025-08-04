package com.team25.event.planner.user.validation;

import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.model.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleBasedRegistrationValidator implements ConstraintValidator<ValidRoleBasedRegistration, UserRequestDTO> {

    @Override
    public boolean isValid(UserRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getUserRole() == null) {
            // If user role is marked as optional, then both role-specific fields are optional.
            // Use @NotNull annotation on Role to trigger this validation.
            return true;
        }

        boolean isValid = switch (dto.getUserRole()) {
            case EVENT_ORGANIZER -> dto.getEventOrganizerFields() != null;
            case OWNER -> dto.getOwnerFields() != null;
            default -> true;
        };

        if (!isValid) {
            // Customize validation message for specific field
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Fields for " + dto.getUserRole() + " are required.")
                    .addPropertyNode(dto.getUserRole() == UserRole.EVENT_ORGANIZER
                            ? "eventOrganizerFields"
                            : "ownerFields"
                    )
                    .addConstraintViolation();
        }

        return isValid;
    }
}