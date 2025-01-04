package com.team25.event.planner.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequestDTO {
    private Long reportedUserId;

    @NotNull(message = "Report message is required")
    private String reportMessage;
}
