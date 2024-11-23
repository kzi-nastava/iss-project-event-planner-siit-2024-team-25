package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.EventFilterDTO;
import com.team25.event.planner.event.dto.EventRequestDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.mapper.EventMapper;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.event.specification.EventSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventMapper eventMapper;
    private final EventSpecification eventSpecification;

    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));
        return eventMapper.toDTO(event);
    }

    public Page<EventResponseDTO> getEvents(EventFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Event> spec = eventSpecification.createSpecification(filter);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return eventRepository.findAll(spec, pageable).map(eventMapper::toDTO);
    }

    // More complex validation is handled in a service method instead of validation annotations.
    // Note that this can also be achieved using custom validators.
    private void validateDto(@Valid EventRequestDTO eventDto) {
        if (eventDto.getEndDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestError("Event must be scheduled in the future");
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new InvalidRequestError("Start date must be before end date");
        }

        if (eventDto.getStartTime().isAfter(eventDto.getEndTime())) {
            throw new InvalidRequestError("Start time must be before end time");
        }
    }

    public EventResponseDTO createEvent(@Valid EventRequestDTO eventDto) {
        validateDto(eventDto);

        EventType eventType = eventTypeRepository.findById(eventDto.getEventTypeId())
                .orElseThrow(() -> new NotFoundError("Event type not found"));

        Event event = eventMapper.toEvent(eventDto, eventType);
        event = eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public EventResponseDTO updateEvent(Long id, @Valid EventRequestDTO eventDto) {
        validateDto(eventDto);

        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));

        // only certain fields are allowed to change
        event.setDescription(eventDto.getDescription());
        event.setMaxParticipants(eventDto.getMaxParticipants());

        event = eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
