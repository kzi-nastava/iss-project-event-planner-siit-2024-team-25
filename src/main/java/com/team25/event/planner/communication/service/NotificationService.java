package com.team25.event.planner.communication.service;

import com.team25.event.planner.communication.dto.NotificationFilterDTO;
import com.team25.event.planner.communication.dto.NotificationResponseDTO;
import com.team25.event.planner.communication.mapper.NotificationMapper;
import com.team25.event.planner.communication.model.Notification;
import com.team25.event.planner.communication.model.NotificationCategory;
import com.team25.event.planner.communication.repository.NotificationRepository;
import com.team25.event.planner.communication.specification.NotificationSpeficition;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSpeficition notificationSpeficition;
    private final NotificationMapper notificationMapper;

    public Page<NotificationResponseDTO> getNotifications(NotificationFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
//        Specification<Notification> spec = notificationSpeficition.createSpecification(filter);
//
//        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//
//        return notificationRepository.findAll(spec, pageable).map(notificationMapper::toDTO);

        Notification notification = new Notification(
                1L,
                "Event has been updated!",
                false,
                123L,
                NotificationCategory.SERVICE,
                Instant.now()
        );
        NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);

        List<NotificationResponseDTO> notificationResponseDTOS = new ArrayList<>();
        notificationResponseDTOS.add(notificationResponseDTO);

        return new PageImpl<>(notificationResponseDTOS);
    }
}
