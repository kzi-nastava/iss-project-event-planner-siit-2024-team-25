package com.team25.event.planner.event.controller;

import com.team25.event.planner.common.dto.ResourceResponseDTO;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.service.EventService;
import com.team25.event.planner.security.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/{id}")
    @PreAuthorize("@eventPermissionEvaluator.canView(authentication, #id)")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id, @RequestParam(required = false) String invitationCode) {
        return ResponseEntity.ok(eventService.getEventById(id, invitationCode));
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

    @GetMapping("/attending/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<List<EventPreviewResponseDTO>> getAttendingEvents(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
            ) {
        return ResponseEntity.ok(eventService.getAttendingEventsOverlappingDateRange(userId, startDate, endDate));
    }

    @GetMapping("/organizer/{organizerId}")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #organizerId")
    public ResponseEntity<List<EventPreviewResponseDTO>> getOrganizerEvents(
            @PathVariable Long organizerId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return ResponseEntity.ok(eventService.getOrganizerEventsOverlappingDateRange(organizerId, startDate, endDate));
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

    @GetMapping("/{id}/report")
    @PreAuthorize("@eventPermissionEvaluator.canView(authentication, #id)")
    public ResponseEntity<Resource> getEventReport(@PathVariable Long id, @RequestParam(required = false) String invitationCode) {
        ResourceResponseDTO resourceResponse = eventService.getEventReport(id, invitationCode);
        return ResponseEntity.ok()
                .contentType(resourceResponse.getMimeType())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(resourceResponse.getFilename())
                        .build().toString()
                )
                .body(resourceResponse.getResource());
    }

    @GetMapping("/{eventId}/attending/{userId}")
    @PreAuthorize("authentication.principal.userId == #userId")
    public ResponseEntity<Boolean> isAttending(@PathVariable Long eventId, @PathVariable Long userId) {
        return ResponseEntity.ok(eventService.isAttending(eventId, userId));
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<EventPreviewResponseDTO> joinEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody JoinEventRequestDTO joinRequest
    ) {
        return ResponseEntity.ok(eventService.joinEvent(eventId, joinRequest.getUserId()));
    }
}
