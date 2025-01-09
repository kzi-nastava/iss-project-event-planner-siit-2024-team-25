package com.team25.event.planner.communication.dto;

import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageResponseDTO {
    private String id;
    private String chatId;
    private UserResponseDTO sender;
    private UserResponseDTO receiver;
    private String content;
    private Date timestamp;

}
