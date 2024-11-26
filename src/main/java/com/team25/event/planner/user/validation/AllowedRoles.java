package com.team25.event.planner.user.validation;

import com.team25.event.planner.user.model.UserRole;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AllowedRolesValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedRoles {
    String message() default "Role is not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    UserRole[] value();
}
