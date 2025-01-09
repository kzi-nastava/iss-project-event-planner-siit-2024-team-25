package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.service.EventService;
import com.team25.event.planner.security.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/{id}")
    @PreAuthorize("@eventPermissionEvaluator.canView(authentication, #id)")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id, @RequestParam(required = false) String invitationCode) {
        return ResponseEntity.ok(eventService.getEventById(id,invitationCode));
    }

    @GetMapping("/")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<Page<EventPreviewResponseDTO>> getMyEvents(
            @ModelAttribute EventFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(eventService.getOrganizerEvents(filter, page, size, sortBy, sortDirection));
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
    ) {
        return ResponseEntity.ok(eventService.getTopEvents());
    }


    @PostMapping
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<EventResponseDTO> createEvent(
            @Valid @RequestBody EventRequestDTO eventDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDto, userDetails.getUserId()));
    }

    @PutMapping("/{id}")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO eventDto
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDto));
    }

    @PostMapping("/{eventId}/send-invitations")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<Void> sendInvitations(
            @PathVariable("eventId") Long eventId,
            @RequestBody List<EventInvitationRequestDTO> requestDTO
    ) {
        eventService.sendInvitations(eventId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/agenda")
    @PreAuthorize("@eventPermissionEvaluator.canView(authentication, #id)")
    public ResponseEntity<List<ActivityResponseDTO>> getAgenda(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventAgenda(id));
    }

    @PostMapping("/{id}/agenda")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<ActivityResponseDTO> addActivityToAgenda(
            @PathVariable("id") Long eventId,
            @Valid @RequestBody ActivityRequestDTO activityRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventService.addActivityToAgenda(eventId, activityRequestDTO)
        );
    }

    @DeleteMapping("/{eventId}/agenda/{activityId}")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<Void> removeActivityFromAgenda(@PathVariable Long eventId, @PathVariable Long activityId) {
        eventService.removeActivityFromAgenda(eventId, activityId);
        return ResponseEntity.noContent().build();
    }
}
