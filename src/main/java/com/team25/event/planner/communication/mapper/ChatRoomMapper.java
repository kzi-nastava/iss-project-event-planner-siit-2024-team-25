package com.team25.event.planner.communication.mapper;

import com.team25.event.planner.communication.dto.ChatRoomResponseDTO;
import com.team25.event.planner.communication.model.ChatRoom;
import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {
    @Mapping(target = "sender", source = "sender", qualifiedByName = "toSenderDTO")
    @Mapping(target = "receiver", source = "receiver", qualifiedByName = "toReceiverDTO")
    ChatRoomResponseDTO toChatRoomResponseDTO(ChatRoom chatRoom);

    @Named("toSenderDTO")
    default UserResponseDTO toSenderDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getProfilePictureUrl(), user.getUserRole());
    }

    @Named("toReceiverDTO")
    default UserResponseDTO toReceiverDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getProfilePictureUrl(), user.getUserRole());
    }
}
