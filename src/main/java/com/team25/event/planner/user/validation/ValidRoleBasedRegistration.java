package com.team25.event.planner.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the userRole is EVENT_ORGANIZER marks organizer-specific fields are mandatory.
 * If the userRole is OWNER marks owner-specific fields as mandatory.
 */
@Constraint(validatedBy = RoleBasedRegistrationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoleBasedRegistration {
    String message() default "Invalid fields for the selected role";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}