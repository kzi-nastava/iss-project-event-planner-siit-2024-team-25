package com.team25.event.planner.communication.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.UnauthorizedError;
import com.team25.event.planner.communication.dto.NotificationFilterDTO;
import com.team25.event.planner.communication.dto.NotificationRequestDTO;
import com.team25.event.planner.communication.dto.NotificationResponseDTO;
import com.team25.event.planner.communication.mapper.NotificationMapper;
import com.team25.event.planner.communication.model.Notification;
import com.team25.event.planner.communication.model.NotificationCategory;
import com.team25.event.planner.communication.repository.NotificationRepository;
import com.team25.event.planner.communication.specification.NotificationSpeficition;
import com.team25.event.planner.security.user.UserDetailsImpl;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSpeficition notificationSpeficition;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public Page<NotificationResponseDTO> getNotifications(NotificationFilterDTO filter, int page, int size, String sortBy, String sortDirection, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundError("User not found"));
        Long currentUserId = currentUserService.getCurrentUserId();
        if(!Objects.equals(user.getId(), currentUserId)){
            throw new UnauthorizedError("Unauthorized access");
        }
        Specification<Notification> spec = notificationSpeficition.createSpecification(filter,user);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return notificationRepository.findAll(spec, pageable).map(notificationMapper::toDTO);
    }

    public void sendNotification(String title, String message, Long entityId,NotificationCategory notificationCategory, User user) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .entityId(entityId)
                .notificationCategory(notificationCategory)
                .user(user)
                .isViewed(false)
                .build();
        notificationRepository.save(notification);
        NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
        messagingTemplate.convertAndSend("/notifications/user/"+user.getId().toString(), notificationResponseDTO);
    }


    public NotificationResponseDTO updateNotification(@Valid  NotificationRequestDTO requestDTO) {
        Notification notification = notificationRepository.findById(requestDTO.getId()).orElseThrow(()->new NotFoundError("Notification not found"));
        notification.setIsViewed(requestDTO.getIsViewed());
        notificationRepository.save(notification);
        return notificationMapper.toDTO(notification);
    }
}
