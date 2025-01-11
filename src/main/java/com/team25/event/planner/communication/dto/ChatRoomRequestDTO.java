package com.team25.event.planner.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomRequestDTO {
    Long senderId;
    Long receiverId;
}
