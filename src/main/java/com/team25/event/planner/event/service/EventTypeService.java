package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.EventTypeRequestDTO;
import com.team25.event.planner.event.dto.EventTypeResponseDTO;
import com.team25.event.planner.event.mapper.EventTypeMapper;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeService {
    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;

    public EventTypeResponseDTO getEventTypeById(Long id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Event type not found"));
        return eventTypeMapper.toDTO(eventType);
    }

    public List<EventTypeResponseDTO> getEventTypes() {
        return eventTypeRepository.findAll().stream().map(eventTypeMapper::toDTO).toList();
    }

    public EventTypeResponseDTO createEventType(@Valid EventTypeRequestDTO eventTypeDto) {
        EventType eventType = eventTypeMapper.toEventType(eventTypeDto);
        eventType = eventTypeRepository.save(eventType);
        return eventTypeMapper.toDTO(eventType);
    }

    public EventTypeResponseDTO updateEventType(Long id, @Valid EventTypeRequestDTO eventTypeDto) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Event type not found"));

        eventType.setName(eventTypeDto.getName());
        eventType.setDescription(eventTypeDto.getDescription());

        eventType = eventTypeRepository.save(eventType);

        return eventTypeMapper.toDTO(eventType);
    }

    public void deleteEventType(Long id) {
        eventTypeRepository.deleteById(id);
    }
}
