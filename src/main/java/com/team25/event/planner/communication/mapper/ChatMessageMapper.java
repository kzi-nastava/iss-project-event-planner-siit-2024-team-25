package com.team25.event.planner.communication.mapper;

import com.team25.event.planner.communication.dto.ChatMessageResponseDTO;
import com.team25.event.planner.communication.model.ChatMessage;
import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "receiver", source = "receiver")
    ChatMessageResponseDTO toChatMessageResponseDTO(ChatMessage chatMessage);

    default UserResponseDTO toSenderDTO(User user) {
        return new UserResponseDTO(user.getId(),user.getFirstName(),user.getLastName(),user.getProfilePictureUrl(),user.getUserRole());
    }

}
