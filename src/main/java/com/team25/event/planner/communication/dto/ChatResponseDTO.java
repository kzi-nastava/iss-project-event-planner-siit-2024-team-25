package com.team25.event.planner.communication.dto;

import lombok.Data;

@Data
public class ChatResponseDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
}
