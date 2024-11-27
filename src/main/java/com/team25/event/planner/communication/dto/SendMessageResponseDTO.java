package com.team25.event.planner.communication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SendMessageResponseDTO {
    private Long id;
    private String message;
    private Long senderId;
    private LocalDateTime timestamp;
}
