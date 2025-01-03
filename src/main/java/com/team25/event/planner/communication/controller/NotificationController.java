package com.team25.event.planner.communication.controller;

import com.team25.event.planner.communication.dto.NotificationFilterDTO;
import com.team25.event.planner.communication.dto.NotificationRequestDTO;
import com.team25.event.planner.communication.dto.NotificationResponseDTO;
import com.team25.event.planner.communication.model.Notification;
import com.team25.event.planner.communication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/")
    public ResponseEntity<Page<NotificationResponseDTO>> getMyNotifications(
            @ModelAttribute NotificationFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ){
        return ResponseEntity.ok(notificationService.getMyNotifications(filter, page, size, sortBy, sortDirection));
    }

    @PutMapping(value = "/")
    public ResponseEntity<NotificationResponseDTO> updateNotification(@RequestBody NotificationRequestDTO notification) {
        NotificationResponseDTO savedNotification = notificationService.updateNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }

    @Scheduled(cron = "${greeting.cron}")
    public void notifyEventOrganizer(){
        notificationService.sendEventStartsSoonNotificationToEventOrganizer();
    }
}
