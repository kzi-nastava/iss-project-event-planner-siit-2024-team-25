package com.team25.event.planner.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportUpdateRequestDTO {
    @NotNull
    private Long reportId;
    @NotNull
    private Boolean isViewed;
}
