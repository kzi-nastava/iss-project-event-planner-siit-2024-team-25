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
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventAttendanceRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.specification.EventSpecification;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.model.UserRole;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final EventAttendanceRepository eventAttendanceRepository;
    private final OfferingRepository offeringRepository;
    private final EventRepository eventRepository;
    private final EventSpecification eventSpecification;

    private Notification createNotification(String title, String message, Long entityId, NotificationCategory notificationCategory, User user) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .entityId(entityId)
                .notificationCategory(notificationCategory)
                .user(user)
                .isViewed(false)
                .build();

        return notificationRepository.save(notification);
    }

    public void sendEventUpdateNotificationToAllUsers(Event event) {
        String title = event.getName() + " updated";
        String message ="The event '" + event.getName() + "' has been updated. Please, take a look.";
        Long entityId = event.getId();
        eventAttendanceRepository.findByEventId(entityId).stream().forEach(attendance -> {
            Notification notification = this.createNotification(title, message, entityId, NotificationCategory.EVENT, attendance.getAttendee());
            NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
            messagingTemplate.convertAndSend("/notifications/user/"+attendance.getAttendee().getId().toString(), notificationResponseDTO);
        });
    }

    public void sendOfferingCategoryNotificationToAdmin(OfferingCategory offeringCategory) {
        String title = offeringCategory.getName() + " offering category suggestion";
        String message ="The offering category '" + offeringCategory.getName() + "' has been suggested. Please, take a look.";
        Long entityId = offeringCategory.getId();
        userRepository.findByUserRole(UserRole.ADMINISTRATOR).forEach(user -> {
            Notification notification = this.createNotification(title, message, entityId, NotificationCategory.OFFERING_CATEGORY, user);
            NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
            messagingTemplate.convertAndSend("/notifications/user/"+user.getId(), notificationResponseDTO);
        });
    }

    public void sendOfferingCategoryApproveNotificationToOwner(OfferingCategory offeringCategory, Owner owner) {
        String title = offeringCategory.getName() + " approved";
        String message ="The offering category '" + offeringCategory.getName() + "' has been approve. Please, take a look.";
        Long entityId = offeringCategory.getId();
        Notification notification = this.createNotification(title, message, entityId, NotificationCategory.OFFERING_CATEGORY, owner);
        NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
        messagingTemplate.convertAndSend("/notifications/user/"+owner.getId().toString(), notificationResponseDTO);
    }

    public void sendOfferingCategoryUpdateNotificationToOwner(OfferingCategory offeringCategory) {
        String title = offeringCategory.getName() + " updated";
        String message ="The offering category '" + offeringCategory.getName() + "' has been updated. Please, take a look.";
        Long entityId = offeringCategory.getId();
        offeringRepository.findOwnersByOfferingCategoryId(offeringCategory.getId()).forEach(owner ->{
            Notification notification = this.createNotification(title, message, entityId, NotificationCategory.OFFERING_CATEGORY, owner);
            NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
            messagingTemplate.convertAndSend("/notifications/user/"+owner.getId().toString(), notificationResponseDTO);
        });
    }

    public void sendOfferingsCategoryUpdateNotificationToOwner(Offering offering, NotificationCategory notificationCategory) {
        String title = offering.getName() + " updated";
        String message ="The offering category for '" + offering.getName() + "' has been updated. Please, take a look.";
        Long entityId = offering.getId();
        Notification notification = this.createNotification(title, message, entityId, notificationCategory, offering.getOwner());
        NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
        messagingTemplate.convertAndSend("/notifications/user/"+offering.getOwner().getId().toString(), notificationResponseDTO);
    }

    public void sendEventCommentNotificationToEventOrganizer(Event event, NotificationCategory notificationCategory) {
        String title = event.getName() + " update";
        String message ="The event '" + event.getName() + "' has been updated. Please, take a look.";
        Long entityId = event.getId();
        eventAttendanceRepository.findByEventId(entityId).stream().forEach(attendance -> {
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .entityId(entityId)
                    .notificationCategory(notificationCategory)
                    .user(attendance.getAttendee())
                    .isViewed(false)
                    .build();

            notificationRepository.save(notification);
            NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
            messagingTemplate.convertAndSend("/notifications/user/"+attendance.getAttendee().getId().toString(), notificationResponseDTO);
        });
    }

    public void sendOfferingCommentNotificationToOwner(Event event, NotificationCategory notificationCategory) {
        String title = event.getName() + " update";
        String message ="The event '" + event.getName() + "' has been updated. Please, take a look.";
        Long entityId = event.getId();
        eventAttendanceRepository.findByEventId(entityId).stream().forEach(attendance -> {
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .entityId(entityId)
                    .notificationCategory(notificationCategory)
                    .user(attendance.getAttendee())
                    .isViewed(false)
                    .build();

            notificationRepository.save(notification);
            NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
            messagingTemplate.convertAndSend("/notifications/user/"+attendance.getAttendee().getId().toString(), notificationResponseDTO);
        });
    }

    public Page<NotificationResponseDTO> getNotifications(NotificationFilterDTO filter, int page, int size, String sortBy, String sortDirection, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundError("User not found"));
        Long currentUserId = currentUserService.getCurrentUserId();
        if(!Objects.equals(user.getId(), currentUserId)){
            throw new UnauthorizedError("Unauthorized access");
        }
        Specification<Notification> spec = notificationSpeficition.createSpecification(filter,user);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<NotificationResponseDTO> notificationResponseDTOS= notificationRepository.findAll(spec, pageable).map(notificationMapper::toDTO);

        return notificationResponseDTOS;
    }

    public NotificationResponseDTO updateNotification(@Valid  NotificationRequestDTO requestDTO) {
        Notification notification = notificationRepository.findById(requestDTO.getId()).orElseThrow(()->new NotFoundError("Notification not found"));
        notification.setIsViewed(requestDTO.getIsViewed());
        notificationRepository.save(notification);
        return notificationMapper.toDTO(notification);
    }

    public void sendEventStartsSoonNotificationToEventOrganizer() {
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = LocalTime.of(startDateTime.getHour(), startDateTime.getMinute()).plusHours(1);
        Specification<Event> specification = eventSpecification.createEventNotificationSpecification(startDate, startTime);
        eventRepository.findAll(specification).stream().forEach(event ->{
            String title = event.getName() + " starts soon";
            String message ="The event '" + event.getName() + "' will start for 1 hour. Be ready.";
            Long entityId = event.getId();
            Notification notification = this.createNotification(title, message,entityId,NotificationCategory.EVENT,event.getOrganizer());
            NotificationResponseDTO notificationResponseDTO = notificationMapper.toDTO(notification);
            messagingTemplate.convertAndSend("/notifications/user/"+event.getOrganizer().getId().toString(), notificationResponseDTO);
        });
    }
}
