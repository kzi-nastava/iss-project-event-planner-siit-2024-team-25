package com.team25.event.planner.communication.mapper;

import com.team25.event.planner.communication.dto.SendMessageResponseDTO;
import com.team25.event.planner.communication.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "senderId", source = "message.sender.id")
    @Mapping(target = "message", source = "message.content")
    SendMessageResponseDTO toDTO(Message message);
}
