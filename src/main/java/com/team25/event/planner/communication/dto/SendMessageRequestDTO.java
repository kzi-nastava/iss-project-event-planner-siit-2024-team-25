package com.team25.event.planner.communication.dto;

import lombok.Data;

@Data
public class SendMessageRequestDTO {
    private String message;
    private Long senderId;
}
