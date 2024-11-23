package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventPreviewMapper {
    EventPreviewResponseDTO toDTO(Event event);
}
