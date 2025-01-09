package com.team25.event.planner.communication.dto;

import lombok.Data;

@Data
public class ChatRoomRequestDTO {
    Long senderId;
    Long receiverId;
    boolean createNewRoomIfNotExists;
}
