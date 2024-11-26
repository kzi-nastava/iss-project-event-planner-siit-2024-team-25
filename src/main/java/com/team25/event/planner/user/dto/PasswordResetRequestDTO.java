package com.team25.event.planner.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    @NotNull
    private final Long accountId;

    @NotEmpty(message = "Old password is required")
    private final String oldPassword;

    @NotEmpty(message = "Password is required")
    private final String newPassword;
}
