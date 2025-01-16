package com.team25.event.planner.user.controller;

import com.team25.event.planner.user.dto.CalendarEventResponseDTO;
import com.team25.event.planner.user.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping("/api/users/{userId}/calendar")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<List<CalendarEventResponseDTO>> getCalendarEvents(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return ResponseEntity.ok(calendarService.getCalendarEvents(userId, startDate, endDate));
    }
}
