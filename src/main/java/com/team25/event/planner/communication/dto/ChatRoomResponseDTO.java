package com.team25.event.planner.communication.dto;

import com.team25.event.planner.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class ChatRoomResponseDTO {
    private Long id;
    private String chatId;
    private UserResponseDTO sender;
    private UserResponseDTO receiver;
}
