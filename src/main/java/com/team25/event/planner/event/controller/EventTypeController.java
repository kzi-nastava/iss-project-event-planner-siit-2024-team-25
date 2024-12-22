package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.EventTypePreviewResponseDTO;
import com.team25.event.planner.event.dto.EventTypeRequestDTO;
import com.team25.event.planner.event.dto.EventTypeResponseDTO;
import com.team25.event.planner.event.service.EventTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
public class EventTypeController {
    private final EventTypeService eventTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<EventTypeResponseDTO> getEventType(@PathVariable Long id) {
        return ResponseEntity.ok(eventTypeService.getEventTypeById(id));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventTypeResponseDTO> getEventTypeByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventTypeService.getEventTypeByEventId(eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventTypeResponseDTO>> getEventTypes() {
        return ResponseEntity.ok(eventTypeService.getEventTypes());
    }

    @GetMapping(value = "/offering")
    public ResponseEntity<List<EventTypeResponseDTO>> getEventTypesByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(eventTypeService.getEventTypesByIds(ids));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventTypePreviewResponseDTO>> getAllEventTypes() {
        return ResponseEntity.ok(eventTypeService.getAllEventTypes());
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<EventTypeResponseDTO> createEventType(@Valid @RequestBody EventTypeRequestDTO eventTypeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventTypeService.createEventType(eventTypeDto));
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<EventTypeResponseDTO> updateEventType(
            @PathVariable Long id,
            @Valid @RequestBody EventTypeRequestDTO eventTypeDto
    ) {
        return ResponseEntity.ok(eventTypeService.updateEventType(id, eventTypeDto));
    }
}
