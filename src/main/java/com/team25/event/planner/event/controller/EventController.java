package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.EventFilterDTO;
import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.dto.EventRequestDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getEvents(
            @ModelAttribute EventFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(eventService.getEvents(filter, page, size, sortBy, sortDirection));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<EventPreviewResponseDTO>> getAllEvents(
            @ModelAttribute EventFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(eventService.getAllEvents(filter, page, size, sortBy, sortDirection));
    }

    @GetMapping("/top")
    public ResponseEntity<Page<EventPreviewResponseDTO>> getTopEvents(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city
    ) {
        return ResponseEntity.ok(eventService.getTopEvents(country, city));
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO eventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO eventDto
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
