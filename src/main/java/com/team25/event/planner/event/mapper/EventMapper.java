package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.dto.EventRequestDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EventTypeMapper.class)
public interface EventMapper {
    EventResponseDTO toDTO(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "eventRequestDTO.name")
    @Mapping(target = "description", source = "eventRequestDTO.description")
    Event toEvent(EventRequestDTO eventRequestDTO, EventType eventType);

    EventPreviewResponseDTO toEventPreviewResponseDTO(Event event);

}
