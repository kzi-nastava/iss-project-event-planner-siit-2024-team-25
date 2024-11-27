package com.team25.event.planner.communication.mapper;

import com.team25.event.planner.communication.dto.ChatResponseDTO;
import com.team25.event.planner.communication.model.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "senderId", source = "chat.user.id")
    @Mapping(target = "receiverId", source = "chat.eventOrganizer.id")
    ChatResponseDTO toDTO(Chat chat);
}
