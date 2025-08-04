package com.team25.event.planner.user.mapper;

import com.team25.event.planner.user.dto.QuickRegisterRequestDTO;
import com.team25.event.planner.user.dto.RegisterRequestDTO;
import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(UserRequestDTO userRequestDTO);

    UserResponseDTO toDTO(User user);

    RegisterRequestDTO toRegisterRequestDto(QuickRegisterRequestDTO quickRegisterRequestDTO);
}
