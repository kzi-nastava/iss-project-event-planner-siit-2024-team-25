package com.team25.event.planner.communication.mapper;

import com.team25.event.planner.communication.dto.NotificationResponseDTO;
import com.team25.event.planner.communication.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "title", target = "title")
    NotificationResponseDTO toDTO(Notification notification);

    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
