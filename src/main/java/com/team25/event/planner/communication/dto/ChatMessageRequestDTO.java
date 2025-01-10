package com.team25.event.planner.communication.dto;

import com.team25.event.planner.user.model.User;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
}
