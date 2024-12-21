package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.EventTypePreviewResponseDTO;
import com.team25.event.planner.event.dto.EventTypeRequestDTO;
import com.team25.event.planner.event.dto.EventTypeResponseDTO;
import com.team25.event.planner.event.mapper.EventTypeMapper;
import com.team25.event.planner.event.mapper.OfferingCategoryMapper;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeService {
    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final EventRepository eventRepository;

    public EventTypeResponseDTO getEventTypeById(Long id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Event type not found"));
        return eventTypeMapper.toDTO(eventType);
    }

    public EventTypeResponseDTO getEventTypeByEventId(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));
        EventType eventType = eventTypeRepository.findById(event.getEventType().getId())
                .orElseThrow(() -> new NotFoundError("Event type not found"));
        return eventTypeMapper.toDTO(eventType);
    }

    public List<EventTypeResponseDTO> getEventTypes() {
        return eventTypeRepository.findAll().stream().map(eventTypeMapper::toDTO).toList();
    }
    public List<EventTypeResponseDTO> getEventTypesByIds(List<Long> ids) {
        return eventTypeRepository.findAllById(ids).stream().map(eventTypeMapper::toDTO).toList();
    }

    public List<EventTypePreviewResponseDTO> getAllEventTypes() {
        return eventTypeRepository.findAll().stream().map(eventTypeMapper::toPreviewDTO).toList();
    }

    public EventTypeResponseDTO createEventType(@Valid EventTypeRequestDTO eventTypeDto) {
        EventType eventType = eventTypeMapper.toEventType(eventTypeDto);

        List<OfferingCategory> categories = offeringCategoryRepository.findAllById(eventTypeDto.getCategories());
        eventType.setOfferingCategories(categories);

        eventType = eventTypeRepository.save(eventType);
        return eventTypeMapper.toDTO(eventType);
    }

    public EventTypeResponseDTO updateEventType(Long id, @Valid EventTypeRequestDTO eventTypeDto) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Event type not found"));

        List<OfferingCategory> categories = offeringCategoryRepository.findAllById(eventTypeDto.getCategories());

        eventType.setDescription(eventTypeDto.getDescription());
        eventType.setOfferingCategories(categories);
        eventType.setIsActive(eventTypeDto.getIsActive());

        eventType = eventTypeRepository.save(eventType);

        return eventTypeMapper.toDTO(eventType);
    }


}
