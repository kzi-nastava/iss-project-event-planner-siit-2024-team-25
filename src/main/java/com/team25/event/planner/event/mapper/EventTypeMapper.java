package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.EventTypePreviewResponseDTO;
import com.team25.event.planner.event.dto.EventTypeRequestDTO;
import com.team25.event.planner.event.dto.EventTypeResponseDTO;
import com.team25.event.planner.event.model.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventTypeMapper {
    EventTypeResponseDTO toDTO(EventType eventType);

    EventTypePreviewResponseDTO toPreviewDTO(EventType eventType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    EventType toEventType(EventTypeRequestDTO eventTypeRequestDTO);
}
