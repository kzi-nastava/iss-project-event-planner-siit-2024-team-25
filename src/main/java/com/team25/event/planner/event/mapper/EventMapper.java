package com.team25.event.planner.event.mapper;

import com.team25.event.planner.common.mapper.LocationMapper;
import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.dto.EventRequestDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.user.model.EventOrganizer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper(componentModel = "spring", uses = {EventTypeMapper.class, LocationMapper.class, EventMapperHelper.class})
public interface EventMapper {
    @Mapping(target = "organizer.id", source = "organizer.id")
    @Mapping(target = "organizer.name", source = "organizer.fullName")
    EventResponseDTO toDTO(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "eventRequestDTO.name")
    @Mapping(target = "description", source = "eventRequestDTO.description")
    Event toEvent(EventRequestDTO eventRequestDTO, EventType eventType, EventOrganizer organizer);

    @Mapping(target = "startDateTime", expression = "java(combineDateAndTime(event.getStartDate(), event.getStartTime()))")
    @Mapping(target = "country", source = "event.location.country")
    @Mapping(target = "city", source = "event.location.city")
    @Mapping(target = "organizerName", source = "event.organizer.firstName")
    EventPreviewResponseDTO toEventPreviewResponseDTO(Event event);

    default LocalDateTime combineDateAndTime(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return null;
        }
        return LocalDateTime.of(date, time);
    }
}
