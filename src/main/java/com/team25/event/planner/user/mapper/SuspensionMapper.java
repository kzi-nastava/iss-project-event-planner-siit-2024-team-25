package com.team25.event.planner.user.mapper;

import com.team25.event.planner.user.dto.SuspensionResponseDTO;
import com.team25.event.planner.user.model.Suspension;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface SuspensionMapper {

    @Mapping(target = "suspendedUserId", source = "account.user.id")
    @Mapping(target = "suspendedUserFirstName", source = "account.user.firstName")
    @Mapping(target = "suspendedUserLastName", source = "account.user.lastName")
    @Mapping(target = "expirationTime", source = "expirationTime")
    SuspensionResponseDTO toDTO(Suspension suspension);

    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
