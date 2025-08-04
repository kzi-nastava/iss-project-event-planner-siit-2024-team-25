package com.team25.event.planner.communication.dto;

import com.team25.event.planner.communication.model.NotificationCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private Long entityId;
    private NotificationCategory notificationCategory;
    private String message;
    private String title;
    private LocalDateTime createdDate;
    private Boolean isViewed;
}
