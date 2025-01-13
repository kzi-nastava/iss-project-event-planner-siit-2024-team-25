package com.team25.event.planner.event.service;

import com.team25.event.planner.common.dto.LatLongDTO;
import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.common.dto.ResourceResponseDTO;
import com.team25.event.planner.common.exception.*;
import com.team25.event.planner.common.service.GeocodingService;
import com.team25.event.planner.common.util.VerificationCodeGenerator;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.email.service.EmailService;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.mapper.ActivityMapper;
import com.team25.event.planner.event.mapper.EventInvitationMapper;
import com.team25.event.planner.event.mapper.EventMapper;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.*;
import com.team25.event.planner.event.specification.EventSpecification;
import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.EventOrganizerRepository;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.service.CurrentUserService;
import com.team25.event.planner.user.service.UserService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final static int VERIFICATION_CODE_LENGTH = 64;

    private final static String eventDetailsReportFilenameTemplate = "event_$ID_$TIME.pdf";

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventMapper eventMapper;
    private final ActivityMapper activityMapper;
    private final EventSpecification eventSpecification;
    private final AccountRepository accountRepository;
    private final EventInvitationMapper eventInvitationMapper;
    private final EventInvitationRepository eventInvitationRepository;
    private final EventOrganizerRepository eventOrganizerRepository;
    private final CurrentUserService currentUserService;
    private final EmailService emailService;
    private final EventAttendanceRepository eventAttendanceRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;
    private final NotificationService notificationService;
    private final EventReportService eventReportService;

    public EventResponseDTO getEventById(Long id, String invitationCode) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));
        User currentUser = currentUserService.getCurrentUser();
        if(event.getOrganizer().getBlockedUsers().contains(currentUser) || event.getOrganizer().getBlockedByUsers().contains(currentUser)) {
            throw new UnauthorizedError("You must be event organizer or invited user to visit this event page");
        }
        if (event.getPrivacyType() == PrivacyType.PRIVATE) {
            if (Objects.equals(event.getOrganizer().getId(), currentUser.getId())) {
                return eventMapper.toDTO(event);
            }
            if (this.checkInvitation(currentUser.getAccount().getEmail(), invitationCode)) {
                return eventMapper.toDTO(event);
            }
            if (this.checkAttendance(currentUser.getId(), event)) {
                return eventMapper.toDTO(event);
            }
        } else {
            return eventMapper.toDTO(event);
        }
        throw new UnauthorizedError("You must be event organizer or invited user to visit this event page");
    }

    /// Returns specification that allows only events visible to the logged-in user.
    /// Event is considered visible to the current user if:
    /// - Event is public
    /// - User is organizer of the event
    /// - User is attending the event
    ///
    /// @return JPA Specification for filtering out non-visible events.
    public Specification<Event> getVisibilityCriteria() {
        final Long userId = currentUserService.getCurrentUserId();
        if (userId == null) {
            return (root, query, cb)
                    -> cb.equal(root.get("privacyType"), PrivacyType.PUBLIC);
        }
        return (root, query, cb) -> {
            Predicate isPublic = cb.equal(root.get("privacyType"), PrivacyType.PUBLIC);
            Predicate isOrganizer = cb.equal(root.get("organizer").get("id"), userId);
            Predicate isAttendee = cb.isNotEmpty(root.join("attendees", JoinType.LEFT));
            return cb.or(isPublic, isOrganizer, isAttendee);
        };
    }

    public Page<EventPreviewResponseDTO> getOrganizerEvents(EventFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        User user = userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(() -> new NotFoundError("User not found"));
        Account organizer = user.getAccount();
        Specification<Event> spec = eventSpecification.createOrganizerSpecification(filter, organizer);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return eventRepository.findAll(spec, pageable).map(eventMapper::toEventPreviewResponseDTO);
    }


    // More complex validation is handled in a service method instead of validation annotations.
    // Note that this can also be achieved using custom validators.
    private void validateDto(@Valid EventRequestDTO eventDto) {
        if (eventDto.getEndDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestError("Event must be scheduled in the future");
        }

        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new InvalidRequestError("Start date must be before end date");
        }

        if (eventDto.getStartDate().equals(eventDto.getEndDate()) && eventDto.getStartTime().isAfter(eventDto.getEndTime())) {
            throw new InvalidRequestError("Start time must be before end time");
        }
    }

    public EventResponseDTO createEvent(@Valid EventRequestDTO eventDto, Long userId) {
        validateDto(eventDto);

        EventType eventType = null;
        if (eventDto.getEventTypeId() != null) {
            eventType = eventTypeRepository.findById(eventDto.getEventTypeId())
                    .orElseThrow(() -> new NotFoundError("Event type not found"));
        }

        EventOrganizer eventOrganizer = eventOrganizerRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedError("You must be event organizer to create an event"));

        Event event = eventMapper.toEvent(eventDto, eventType, eventOrganizer);

        LatLongDTO latLong = geocodingService.getLatLong(eventDto.getLocation());
        event.getLocation().setLatitude(latLong.getLatitude());
        event.getLocation().setLongitude(latLong.getLongitude());

        event = eventRepository.save(event);

        return eventMapper.toDTO(event);
    }

    public EventResponseDTO updateEvent(Long id, @Valid EventRequestDTO eventDto) {
        validateDto(eventDto);

        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundError("Event not found"));

        Long currentUserId = currentUserService.getCurrentUserId();
        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new UnauthorizedError();
        }

        // only certain fields are allowed to change
        event.setDescription(eventDto.getDescription());
        event.setMaxParticipants(eventDto.getMaxParticipants());

        event = eventRepository.save(event);

        notificationService.sendEventUpdateNotificationToAllUsers(event);

        return eventMapper.toDTO(event);
    }

    public Page<EventPreviewResponseDTO> getAllEvents(EventFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        User currentUser = currentUserService.getCurrentUser();
        Specification<Event> spec = eventSpecification.createSpecification(filter,currentUser);
        spec.and(getVisibilityCriteria());

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return eventRepository.findAll(spec, pageable).map(eventMapper::toEventPreviewResponseDTO);
    }


    public Page<EventPreviewResponseDTO> getTopEvents(LocationResponseDTO location) {
        String sortDirection = "desc";
        String sortBy = "createdDate";
        User user = currentUserService.getCurrentUser();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageable = PageRequest.of(0, 5,Sort.by(direction, sortBy));
        Specification<Event> specification = eventSpecification.createTopEventsSpecification(location, user);
        return eventRepository.findAll(specification, pageable).map(eventMapper::toEventPreviewResponseDTO);
    }

    public void sendInvitations(Long eventId, List<EventInvitationRequestDTO> requestDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));

        Long currentUserId = currentUserService.getCurrentUserId();
        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new UnauthorizedError();
        }

        requestDTO.stream().forEach(eventInvitationRequestDTO -> {
            EventInvitation eventInvitation = eventInvitationMapper.toEventInvitation(eventInvitationRequestDTO, event, EventInvitationStatus.PENDING);
            eventInvitation.setInvitationCode(VerificationCodeGenerator.generateVerificationCode(VERIFICATION_CODE_LENGTH));
            eventInvitationRepository.save(eventInvitation);
            Optional<Account> account = accountRepository.findByEmail(eventInvitationRequestDTO.getGuestEmail());
            if (!account.isEmpty()) {
                EventInvitationEmailDTO eventInvitationEmailDTO = eventInvitationMapper.toEventInvitationEmailDTO(account.get().getUser(), event, eventInvitation.getInvitationCode());
                emailService.sendEventInvitationEmail(account.get().getEmail(), eventInvitationEmailDTO);
            } else {
                EventInvitationShortEmailDTO dto = eventInvitationMapper.toEventInvitationShortEmailDto(event, eventInvitation.getInvitationCode());
                emailService.sendQuickRegisterEmail(eventInvitationRequestDTO.getGuestEmail(), dto);
            }
        });
    }

    public boolean checkInvitation(String guestEmail, String invitationCode) {
        Optional<EventInvitation> eventInvitation = eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(guestEmail, invitationCode);
        if (eventInvitation.isPresent()) {
            if (eventInvitation.get().getStatus() == EventInvitationStatus.PENDING) {
                eventInvitation.get().setStatus(EventInvitationStatus.ACCEPTED);
                eventInvitationRepository.save(eventInvitation.get());
                return true;
            } else if (eventInvitation.get().getStatus() == EventInvitationStatus.ACCEPTED) {
                return true;
            }
            throw new InvalidRequestError("This invitation was already accepted");
        }
        return false;
    }

    public boolean checkAttendance(Long userId, Event event) {
        Optional<EventAttendance> eventAttendance = eventAttendanceRepository.getEventAttendanceByAttendeeIdAndEventId(userId, event.getId());
        if (eventAttendance.isPresent()) {
            return true;
        }
        return false;
    }

    public Event getEventByGuestAndInvitationCode(String guestEmail, String invitationCode) {
        Optional<EventInvitation> eventInvitation = eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(guestEmail, invitationCode);
        if (eventInvitation.isPresent()) {
            return eventInvitation.get().getEvent();
        }
        return null;
    }

    public List<ActivityResponseDTO> getEventAgenda(Long eventId) {
        return activityRepository.findByEventIdOrderByStartTimeAsc(eventId)
                .stream().map(activityMapper::toDTO).toList();
    }

    public ActivityResponseDTO addActivityToAgenda(Long eventId, @Valid ActivityRequestDTO activityRequestDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));

        final Long currentUserId = currentUserService.getCurrentUserId();
        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new UnauthorizedError();
        }

        Activity activity = activityMapper.toActivity(activityRequestDTO);
        activity.setEvent(event);

        activity = activityRepository.save(activity);

        notificationService.sendEventUpdateNotificationToAllUsers(event);

        return activityMapper.toDTO(activity);
    }

    public void removeActivityFromAgenda(Long eventId, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundError("Activity not found"));

        if (!activity.getEvent().getId().equals(eventId)) {
            throw new InvalidRequestError("Invalid activity");
        }

        final Long currentUserId = currentUserService.getCurrentUserId();
        if (!activity.getEvent().getOrganizer().getId().equals(currentUserId)) {
            throw new UnauthorizedError();
        }

        notificationService.sendEventUpdateNotificationToAllUsers(activity.getEvent());
        activityRepository.delete(activity);
    }

    public void createEventAttendance(User user, String invitationCode) {
        EventAttendance eventAttendance = new EventAttendance();
        EventAttendanceId eventAttendanceId = new EventAttendanceId();
        eventAttendanceId.setUserId(user.getId());
        Optional<EventInvitation> eventInvitation = eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(user.getAccount().getEmail(), invitationCode);
        if (eventInvitation.isPresent()) {
            eventAttendanceId.setEventId(eventInvitation.get().getEvent().getId());
            eventAttendance.setId(eventAttendanceId);
            eventAttendance.setEvent(eventInvitation.get().getEvent());
        }
        eventAttendance.setAttendee(user);
        eventAttendanceRepository.save(eventAttendance);
    }

    public ResourceResponseDTO getEventReport(Long eventId, String invitationCode) {
        EventResponseDTO event = getEventById(eventId, invitationCode);
        List<ActivityResponseDTO> agenda = getEventAgenda(eventId);

        try {
            Resource resource = eventReportService.generateEventDetailsReport(event, agenda);

            String filename = eventDetailsReportFilenameTemplate
                    .replace("$ID", event.getId().toString())
                    .replace("$TIME", Long.toString(System.currentTimeMillis()));

            return new ResourceResponseDTO(resource, filename, MediaType.APPLICATION_PDF);

        } catch (ReportGenerationFailedException e) {
            throw new ServerError("Failed to generate report", 500);
        }
    }

    public void blockByEventOrganizer(User organizer, User blockedUser) {
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.now();
        eventInvitationRepository.findEventInvitationsForFutureEvents(organizer.getId(),blockedUser.getAccount().getEmail(), today, time).stream().forEach(eventInvitation -> {
            if(eventInvitation.getStatus() == EventInvitationStatus.PENDING){
                eventInvitation.setStatus(EventInvitationStatus.DENIED);
            }
            eventInvitationRepository.save(eventInvitation);
        });
    }
}
