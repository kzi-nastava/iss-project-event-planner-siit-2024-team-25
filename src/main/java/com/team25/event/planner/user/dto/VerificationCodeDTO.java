package com.team25.event.planner.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VerificationCodeDTO {
    @NotEmpty
    private String verificationCode;
}
