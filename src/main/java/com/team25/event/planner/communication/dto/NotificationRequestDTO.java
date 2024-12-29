package com.team25.event.planner.communication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    @NotNull(message = "Notification Id is required")
    private Long id;
    @NotNull(message = "Is viewed is required")
    private Boolean isViewed;
}
