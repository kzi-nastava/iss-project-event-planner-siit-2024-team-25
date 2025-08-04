package com.team25.event.planner.communication.model;

import com.team25.event.planner.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private Long id;
    private UserResponseDTO sender;
    private UserResponseDTO receiver;
    private String content;
    private String chatId;
    private Date timestamp;
}
