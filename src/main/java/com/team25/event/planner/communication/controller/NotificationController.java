package com.team25.event.planner.communication.controller;

import com.team25.event.planner.communication.dto.NotificationFilterDTO;
import com.team25.event.planner.communication.dto.NotificationResponseDTO;
import com.team25.event.planner.communication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/{userId}")
    @PreAuthorize("authentication.principal.userId = #userId")
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(
            @PathVariable("userId") Long userId,
            @ModelAttribute NotificationFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        return ResponseEntity.ok(notificationService.getNotifications(filter, page, size, sortBy, sortDirection));
    }
}
