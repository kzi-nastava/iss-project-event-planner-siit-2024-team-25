package com.team25.event.planner.service;

import com.team25.event.planner.common.dto.LatLongDTO;
import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.UnauthorizedError;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.common.service.GeocodingService;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.mapper.ActivityMapper;
import com.team25.event.planner.event.mapper.EventMapper;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.*;
import com.team25.event.planner.event.service.EventService;
import com.team25.event.planner.event.specification.EventSpecification;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.EventOrganizerRepository;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.service.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EventServiceTest {
    @Autowired
    private EventService eventService;

    @MockitoBean
    private EventRepository eventRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private EventSpecification eventSpecification;

    @MockitoBean
    private EventAttendanceRepository eventAttendanceRepository;

    @MockitoBean
    private EventInvitationRepository eventInvitationRepository;

    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private ActivityRepository activityRepository;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private ActivityMapper activityMapper;

    @MockitoBean
    private EventTypeRepository eventTypeRepository;

    @MockitoBean
    private EventOrganizerRepository eventOrganizerRepository;

    @MockitoBean
    private GeocodingService geocodingService;

    private EventOrganizer eventOrganizer;
    private User regularUser;
    private Event publicEvent;
    private Event privateEvent;
    private EventResponseDTO publicEventResponseDto;
    private EventResponseDTO privateEventResponseDto;
    private EventPreviewResponseDTO publicEventPreviewDto;
    private EventPreviewResponseDTO privateEventPreviewDto;
    private EventFilterDTO emptyFilterDto;

    private EventRequestDTO validEventRequestDto;
    private EventType eventType;
    private LatLongDTO latLongDto;

    private Activity activity1;
    private Activity activity2;
    private ActivityResponseDTO activityDto1;
    private ActivityResponseDTO activityDto2;
    private ActivityRequestDTO activityRequestDto;

    @BeforeEach
    public void setup() {
        Account account1 = new Account(null, "account1@example.com", "password1", AccountStatus.ACTIVE, null, null);
        Location location = new Location("Serbia", "Belgrade", "Bulevar Kralja Aleksandra 10", 44.0, 20.0);
        PhoneNumber phone = new PhoneNumber("+381601234567");

        eventOrganizer = new EventOrganizer(
                1L,
                "John",
                "Doe",
                "https://example.com/johndoe.jpg",
                UserRole.EVENT_ORGANIZER,
                account1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                location,
                phone
        );

        EventOrganizerPreviewResponseDTO eventOrganizerPreviewDto = new EventOrganizerPreviewResponseDTO(eventOrganizer.getId(), eventOrganizer.getFullName());

        OfferingCategory offeringCategory = new OfferingCategory(1L, "Conference Catering category", "", OfferingCategoryType.ACCEPTED);
        ArrayList<OfferingCategory> offeringCategories = new ArrayList<>();
        offeringCategories.add(offeringCategory);
        EventType conferenceType = new EventType(1L, "Description", "Conference", true, offeringCategories);

        LocalDate eventStartDate = LocalDate.now().plusYears(1).withDayOfMonth(1);
        LocalDate eventEndDate = LocalDate.now().plusYears(1).withDayOfMonth(1);
        LocalTime eventStartTime = LocalTime.of(9, 0);
        LocalTime eventEndTime = LocalTime.of(18, 0);

        publicEvent = new Event(1L, conferenceType, "Tech Conference 2024", "A conference about technology", 200, PrivacyType.PUBLIC,
                eventStartDate, eventEndDate, eventStartTime, eventEndTime, location, eventOrganizer, Instant.now(), null, null, null);
        privateEvent = new Event(2L, conferenceType, "Tech Conference 2025", "A conference about technology", 200, PrivacyType.PRIVATE,
                eventStartDate, eventEndDate, eventStartTime, eventEndTime, location, eventOrganizer, Instant.now(), null, null, null);

        publicEventResponseDto = new EventResponseDTO(
                publicEvent.getId(),
                new EventTypePreviewResponseDTO(conferenceType.getId(), conferenceType.getName()),
                publicEvent.getName(), publicEvent.getDescription(), publicEvent.getMaxParticipants(), publicEvent.getPrivacyType(),
                publicEvent.getStartDate(), publicEvent.getEndDate(), publicEvent.getStartTime(), publicEvent.getEndTime(),
                new LocationResponseDTO(location.getCountry(), location.getCity(), location.getAddress(), location.getLatitude(), location.getLongitude()),
                eventOrganizerPreviewDto, false
        );

        privateEventResponseDto = new EventResponseDTO(
                privateEvent.getId(),
                new EventTypePreviewResponseDTO(conferenceType.getId(), conferenceType.getName()),
                privateEvent.getName(), privateEvent.getDescription(), privateEvent.getMaxParticipants(), privateEvent.getPrivacyType(),
                privateEvent.getStartDate(), privateEvent.getEndDate(), privateEvent.getStartTime(), privateEvent.getEndTime(),
                new LocationResponseDTO(location.getCountry(), location.getCity(), location.getAddress(), location.getLatitude(), location.getLongitude()),
                eventOrganizerPreviewDto, false
        );

        publicEventPreviewDto = new EventPreviewResponseDTO(
                publicEvent.getId(),
                publicEvent.getName(), publicEvent.getDescription(),
                LocalDateTime.of(publicEvent.getStartDate(), publicEvent.getStartTime()),
                location.getCountry(), location.getCity(),
                eventOrganizer.getFirstName(), eventOrganizer.getLastName(),
                false
        );

        privateEventPreviewDto = new EventPreviewResponseDTO(
                privateEvent.getId(),
                privateEvent.getName(), privateEvent.getDescription(),
                LocalDateTime.of(privateEvent.getStartDate(), privateEvent.getStartTime()),
                location.getCountry(), location.getCity(),
                eventOrganizer.getFirstName(), eventOrganizer.getLastName(),
                false
        );

        when(eventMapper.toDTO(publicEvent)).thenReturn(publicEventResponseDto);
        when(eventMapper.toDTO(privateEvent)).thenReturn(privateEventResponseDto);
        when(eventMapper.toEventPreviewResponseDTO(publicEvent)).thenReturn(publicEventPreviewDto);
        when(eventMapper.toEventPreviewResponseDTO(privateEvent)).thenReturn(privateEventPreviewDto);

        emptyFilterDto = EventFilterDTO.builder().build();

        Account account2 = new Account(null, "account2@example.com", "password1", AccountStatus.ACTIVE, null, null);
        regularUser = new User(
                2L,
                "Paul",
                "Smith",
                null,
                UserRole.REGULAR,
                account2,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        eventType = new EventType(1L, "Description", "Conference", true, offeringCategories);
        latLongDto = new LatLongDTO(44.0, 20.0);

        LocationRequestDTO locationRequestDto = new LocationRequestDTO("Serbia", "Belgrade", "Bulevar Kralja Aleksandra 10");

        validEventRequestDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Tech Conference 2025")
                .description("A conference about technology")
                .maxParticipants(200)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusMonths(6))
                .endDate(LocalDate.now().plusMonths(6))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .location(locationRequestDto)
                .build();
    }

    /// Setup method used only in agenda test and thus not included in setup() in order not to clutter it and improve performance
    public void setupAgendaTestData() {
        activity1 = new Activity(
                1L,
                "Event Opening",
                "The opening ceremony",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "Large hall",
                publicEvent
        );

        activity2 = new Activity(
                2L,
                "Lecture",
                "Lecture by prof. Black Smith",
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(4),
                "Conference room",
                publicEvent
        );

        activityDto1 = new ActivityResponseDTO(activity1.getId(), activity1.getName(), activity1.getDescription(), activity1.getStartTime(), activity1.getEndTime(), activity1.getLocation());
        activityDto2 = new ActivityResponseDTO(activity2.getId(), activity2.getName(), activity2.getDescription(), activity2.getStartTime(), activity2.getEndTime(), activity2.getLocation());

        when(activityMapper.toDTO(activity1)).thenReturn(activityDto1);
        when(activityMapper.toDTO(activity2)).thenReturn(activityDto2);

        activityRequestDto = ActivityRequestDTO.builder()
                .name("Workshop")
                .description("Hands-on coding")
                .startTime(LocalDateTime.now().plusHours(4))
                .endTime(LocalDateTime.now().plusHours(6))
                .build();
    }

    @Test
    @DisplayName("Get event by ID OK")
    public void testGetEventById_public() {
        Long eventId = publicEvent.getId();
        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);

        EventResponseDTO eventDto = eventService.getEventById(publicEvent.getId(), null);
        assertThat(eventDto).usingRecursiveAssertion().isEqualTo(publicEventResponseDto);
    }

    @Test
    @DisplayName("Get event by ID - not found")
    public void testGetEventById_notFound() {
        Long eventId = 3L;
        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.empty());

        NotFoundError error = assertThrows(NotFoundError.class, () -> eventService.getEventById(eventId, null));
        assertEquals("Event not found", error.getMessage());
    }

    @Test
    @DisplayName("Get event by ID - invited")
    public void testGetEventById_invited() {
        Long eventId = privateEvent.getId();
        String email = regularUser.getAccount().getEmail();
        String invitationCode = "INVITATION_CODE";

        EventInvitation invitation = new EventInvitation(1L, email, invitationCode, EventInvitationStatus.ACCEPTED, privateEvent);

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(privateEvent));
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);
        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.of(invitation));

        EventResponseDTO eventDto = assertDoesNotThrow(() -> eventService.getEventById(eventId, invitationCode));
        assertThat(eventDto).usingRecursiveAssertion().isEqualTo(privateEventResponseDto);
    }

    @Test
    @DisplayName("Get event by ID - invalid invitation")
    public void testGetEventById_invalidInvitation() {
        Long eventId = privateEvent.getId();
        String email = regularUser.getAccount().getEmail();
        String invitationCode = "INVITATION_CODE";
        String invalidInvitationCode = "INVALID_INVITATION_CODE";

        EventInvitation invitation = new EventInvitation(1L, email, invitationCode, EventInvitationStatus.ACCEPTED, privateEvent);

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(privateEvent));
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);
        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invalidInvitationCode)))
                .thenReturn(Optional.empty());
        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.of(invitation));
        when(eventAttendanceRepository.getEventAttendanceByAttendeeIdAndEventId(eq(regularUser.getId()), any()))
                .thenReturn(Optional.empty());

        UnauthorizedError error = assertThrows(UnauthorizedError.class, () -> eventService.getEventById(eventId, invalidInvitationCode));
        assertEquals("You must be event organizer or invited user to visit this event page", error.getMessage());
    }

    @Test
    @DisplayName("Get event by ID - attending private event")
    public void testGetEventById_attending() {
        Long eventId = privateEvent.getId();
        Long userId = regularUser.getId();
        EventAttendance eventAttendance = new EventAttendance(new EventAttendanceId(userId, eventId), regularUser, privateEvent);

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(privateEvent));
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);
        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(any(), any()))
                .thenReturn(Optional.empty());
        when(eventAttendanceRepository.getEventAttendanceByAttendeeIdAndEventId(eq(userId), eq(eventId)))
                .thenReturn(Optional.of(eventAttendance));

        EventResponseDTO eventDto = assertDoesNotThrow(() -> eventService.getEventById(eventId, null));
        assertThat(eventDto).usingRecursiveAssertion().isEqualTo(privateEventResponseDto);
    }

    @Test
    @DisplayName("Get event by ID - forbidden")
    public void testGetEventById_forbidden() {
        Long eventId = privateEvent.getId();
        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(privateEvent));
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);
        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(any(), any()))
                .thenReturn(Optional.empty());
        when(eventAttendanceRepository.getEventAttendanceByAttendeeIdAndEventId(eq(regularUser.getId()), any()))
                .thenReturn(Optional.empty());
        UnauthorizedError error = assertThrows(UnauthorizedError.class, () -> eventService.getEventById(eventId, null));
        assertEquals("You must be event organizer or invited user to visit this event page", error.getMessage());
    }

    @Test
    @DisplayName("Get event by ID - blocked")
    public void testGetEventById_blocked() {
        Long eventId = publicEvent.getId();
        eventOrganizer.getBlockedUsers().add(regularUser);

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);

        UnauthorizedError error = assertThrows(UnauthorizedError.class, () -> eventService.getEventById(eventId, null));
        assertEquals("You must be event organizer or invited user to visit this event page", error.getMessage());
    }

    @Test
    @DisplayName("Get organizer events")
    public void testGetOrganizerEvents_ok() {
        when(currentUserService.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(eventOrganizer));
        Specification<Event> spec = Specification.where(null);
        when(eventSpecification.createOrganizerSpecification(eq(emptyFilterDto), eq(eventOrganizer)))
                .thenReturn(spec);
        when(eventRepository.findAll(eq(spec), (Pageable) any()))
                .thenReturn(new PageImpl<>(List.of(publicEvent, privateEvent)));

        Page<EventPreviewResponseDTO> events = eventService.getOrganizerEvents(emptyFilterDto, 0, 5, "startDate", "asc");
        assertEquals(2, events.getContent().size());
        assertThat(events.getContent().get(0)).usingRecursiveAssertion().isEqualTo(publicEventPreviewDto);
        assertThat(events.getContent().get(1)).usingRecursiveAssertion().isEqualTo(privateEventPreviewDto);
    }

    @Test
    @DisplayName("Get all events - success")
    public void testGetAllEvents_success() {
        when(currentUserService.getCurrentUser()).thenReturn(regularUser);
        Specification<Event> spec = Specification.where(null);
        when(eventSpecification.createSpecification(eq(emptyFilterDto), eq(regularUser)))
                .thenReturn(spec);
        when(eventRepository.findAll(eq(spec), (Pageable) any()))
                .thenReturn(new PageImpl<>(List.of(publicEvent)));

        Page<EventPreviewResponseDTO> events = eventService.getAllEvents(emptyFilterDto, 0, 5, "startDate", "asc");
        assertEquals(1, events.getContent().size());
        assertThat(events.getContent().getFirst()).usingRecursiveAssertion().isEqualTo(publicEventPreviewDto);
    }

    @Test
    @DisplayName("Check invitation - pending invitation")
    public void testCheckInvitation_pending() {
        String email = "test@example.com";
        String invitationCode = "INVITATION123";
        EventInvitation invitation = new EventInvitation(1L, email, invitationCode, EventInvitationStatus.PENDING, publicEvent);

        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.of(invitation));
        when(eventInvitationRepository.save(any(EventInvitation.class))).thenReturn(invitation);

        boolean result = eventService.checkInvitation(email, invitationCode);
        assertTrue(result);
        assertEquals(EventInvitationStatus.ACCEPTED, invitation.getStatus());
    }

    @Test
    @DisplayName("Check invitation - already accepted")
    public void testCheckInvitation_alreadyAccepted() {
        String email = "test@example.com";
        String invitationCode = "INVITATION123";
        EventInvitation invitation = new EventInvitation(1L, email, invitationCode, EventInvitationStatus.ACCEPTED, publicEvent);

        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.of(invitation));

        boolean result = eventService.checkInvitation(email, invitationCode);
        assertTrue(result);
    }

    @Test
    @DisplayName("Check invitation - already declined")
    public void testCheckInvitation_alreadyDeclined() {
        String email = "test@example.com";
        String invitationCode = "INVITATION123";
        EventInvitation invitation = new EventInvitation(1L, email, invitationCode, EventInvitationStatus.DENIED, publicEvent);

        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.of(invitation));

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.checkInvitation(email, invitationCode));
        assertEquals("This invitation was already accepted", error.getMessage());
    }

    @Test
    @DisplayName("Check invitation - not found")
    public void testCheckInvitation_notFound() {
        String email = "test@example.com";
        String invitationCode = "INVALID123";

        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.empty());

        boolean result = eventService.checkInvitation(email, invitationCode);
        assertFalse(result);
    }

    @Test
    @DisplayName("Check attendance - user is attending")
    public void testCheckAttendance_userAttending() {
        Long userId = regularUser.getId();
        Long eventId = publicEvent.getId();
        EventAttendance attendance = new EventAttendance(new EventAttendanceId(userId, eventId), regularUser, publicEvent);

        when(eventAttendanceRepository.getEventAttendanceByAttendeeIdAndEventId(eq(userId), eq(eventId)))
                .thenReturn(Optional.of(attendance));

        boolean result = eventService.checkAttendance(userId, publicEvent);
        assertTrue(result);
    }

    @Test
    @DisplayName("Check attendance - user not attending")
    public void testCheckAttendance_userNotAttending() {
        Long userId = regularUser.getId();
        Long eventId = publicEvent.getId();

        when(eventAttendanceRepository.getEventAttendanceByAttendeeIdAndEventId(eq(userId), eq(eventId)))
                .thenReturn(Optional.empty());

        boolean result = eventService.checkAttendance(userId, publicEvent);
        assertFalse(result);
    }

    @Test
    @DisplayName("Get event by guest and invitation code - found")
    public void testGetEventByGuestAndInvitationCode_found() {
        String email = "test@example.com";
        String invitationCode = "INVITATION123";
        EventInvitation invitation = new EventInvitation(1L, email, invitationCode, EventInvitationStatus.ACCEPTED, publicEvent);

        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.of(invitation));

        Event result = eventService.getEventByGuestAndInvitationCode(email, invitationCode);
        assertEquals(publicEvent, result);
    }

    @Test
    @DisplayName("Get event by guest and invitation code - not found")
    public void testGetEventByGuestAndInvitationCode_notFound() {
        String email = "test@example.com";
        String invitationCode = "INVALID123";

        when(eventInvitationRepository.findEventInvitationByGuestEmailAndInvitationCode(eq(email), eq(invitationCode)))
                .thenReturn(Optional.empty());

        Event result = eventService.getEventByGuestAndInvitationCode(email, invitationCode);
        assertNull(result);
    }

    @Test
    @DisplayName("Get visibility criteria - authenticated user")
    public void testGetVisibilityCriteria_authenticatedUser() {
        when(currentUserService.getCurrentUserId()).thenReturn(1L);

        Specification<Event> spec = eventService.getVisibilityCriteria();
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Get visibility criteria - anonymous user")
    public void testGetVisibilityCriteria_anonymousUser() {
        when(currentUserService.getCurrentUserId()).thenReturn(null);

        Specification<Event> spec = eventService.getVisibilityCriteria();
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Create event - success")
    public void testCreateEvent_success() {
        when(eventTypeRepository.findById(eq(1L))).thenReturn(Optional.of(eventType));
        when(eventOrganizerRepository.findById(eq(eventOrganizer.getId()))).thenReturn(Optional.of(eventOrganizer));
        when(geocodingService.getLatLong(any())).thenReturn(latLongDto);
        when(eventMapper.toEvent(eq(validEventRequestDto), eq(eventType), eq(eventOrganizer))).thenReturn(publicEvent);
        when(eventRepository.save(eq(publicEvent))).thenReturn(publicEvent);

        EventResponseDTO result = eventService.createEvent(validEventRequestDto, eventOrganizer.getId());

        assertThat(result).usingRecursiveAssertion().isEqualTo(publicEventResponseDto);
        verify(eventRepository).save(eq(publicEvent));
        verify(geocodingService).getLatLong(eq(validEventRequestDto.getLocation()));
    }

    @Test
    @DisplayName("Create event - event type not found")
    public void testCreateEvent_eventTypeNotFound() {
        when(eventTypeRepository.findById(eq(1L))).thenReturn(Optional.empty());

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> eventService.createEvent(validEventRequestDto, eventOrganizer.getId()));
        assertEquals("Event type not found", error.getMessage());
    }

    @Test
    @DisplayName("Create event - user is not event organizer")
    public void testCreateEvent_userNotEventOrganizer() {
        when(eventTypeRepository.findById(eq(1L))).thenReturn(Optional.of(eventType));
        when(eventOrganizerRepository.findById(eq(regularUser.getId()))).thenReturn(Optional.empty());

        UnauthorizedError error = assertThrows(UnauthorizedError.class,
                () -> eventService.createEvent(validEventRequestDto, regularUser.getId()));
        assertEquals("You must be event organizer to create an event", error.getMessage());
    }

    @Test
    @DisplayName("Create event - end date in past")
    public void testCreateEvent_endDateInPast() {
        EventRequestDTO pastEventDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Past Event")
                .description("Event in the past")
                .maxParticipants(100)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.createEvent(pastEventDto, eventOrganizer.getId()));
        assertEquals("Event must be scheduled in the future", error.getMessage());
    }

    @Test
    @DisplayName("Create event - start date after end date")
    public void testCreateEvent_startDateAfterEndDate() {
        EventRequestDTO invalidDateDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Invalid Date Event")
                .description("Event with invalid dates")
                .maxParticipants(100)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.createEvent(invalidDateDto, eventOrganizer.getId()));
        assertEquals("Start date must be before end date", error.getMessage());
    }

    @Test
    @DisplayName("Create event - same date with start time after end time")
    public void testCreateEvent_sameDate_startTimeAfterEndTime() {
        EventRequestDTO invalidTimeDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Invalid Time Event")
                .description("Event with invalid times")
                .maxParticipants(100)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(9, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.createEvent(invalidTimeDto, eventOrganizer.getId()));
        assertEquals("Start time must be before end time", error.getMessage());
    }

    @Test
    @DisplayName("Create event - without event type")
    public void testCreateEvent_withoutEventType() {
        EventRequestDTO noTypeDto = EventRequestDTO.builder()
                .eventTypeId(null)
                .name("No Type Event")
                .description("Event without type")
                .maxParticipants(100)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        when(eventOrganizerRepository.findById(eq(eventOrganizer.getId()))).thenReturn(Optional.of(eventOrganizer));
        when(geocodingService.getLatLong(any())).thenReturn(latLongDto);
        when(eventMapper.toEvent(eq(noTypeDto), eq(null), eq(eventOrganizer))).thenReturn(publicEvent);
        when(eventRepository.save(eq(publicEvent))).thenReturn(publicEvent);
        when(eventMapper.toDTO(eq(publicEvent))).thenReturn(publicEventResponseDto);

        EventResponseDTO result = eventService.createEvent(noTypeDto, eventOrganizer.getId());

        assertThat(result).usingRecursiveAssertion().isEqualTo(publicEventResponseDto);
        verify(eventTypeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Update event - success")
    public void testUpdateEvent_success() {
        EventRequestDTO updateDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Updated Event") // Name won't be updated based on service logic
                .description("Updated description")
                .maxParticipants(250)
                .privacyType(PrivacyType.PRIVATE)
                .startDate(LocalDate.now().plusMonths(6))
                .endDate(LocalDate.now().plusMonths(6))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(19, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        Event updatedEvent = new Event(publicEvent.getId(), publicEvent.getEventType(), publicEvent.getName(),
                "Updated description", 250, publicEvent.getPrivacyType(), publicEvent.getStartDate(),
                publicEvent.getEndDate(), publicEvent.getStartTime(), publicEvent.getEndTime(),
                publicEvent.getLocation(), publicEvent.getOrganizer(), publicEvent.getCreatedDate(),
                publicEvent.getAgenda(), new ArrayList<>(), new ArrayList<>());

        EventResponseDTO updatedEventResponseDto = new EventResponseDTO(
                updatedEvent.getId(),
                new EventTypePreviewResponseDTO(eventType.getId(), eventType.getName()),
                updatedEvent.getName(), "Updated description", 250, updatedEvent.getPrivacyType(),
                updatedEvent.getStartDate(), updatedEvent.getEndDate(), updatedEvent.getStartTime(), updatedEvent.getEndTime(),
                new LocationResponseDTO("", "", "", 0.0, 0.0),
                new EventOrganizerPreviewResponseDTO(eventOrganizer.getId(), eventOrganizer.getFullName()), false
        );

        when(eventRepository.findById(eq(publicEvent.getId()))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(eventMapper.toDTO(eq(updatedEvent))).thenReturn(updatedEventResponseDto);

        EventResponseDTO result = eventService.updateEvent(publicEvent.getId(), updateDto);

        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getMaxParticipants()).isEqualTo(250);
        verify(eventRepository).save(any(Event.class));
        verify(notificationService).sendEventUpdateNotificationToAllUsers(any(Event.class));
    }

    @Test
    @DisplayName("Update event - event not found")
    public void testUpdateEvent_eventNotFound() {
        when(eventRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> eventService.updateEvent(999L, validEventRequestDto));
        assertEquals("Event not found", error.getMessage());
    }

    @Test
    @DisplayName("Update event - unauthorized user")
    public void testUpdateEvent_unauthorizedUser() {
        when(eventRepository.findById(eq(publicEvent.getId()))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(regularUser.getId()); // Different user

        UnauthorizedError error = assertThrows(UnauthorizedError.class,
                () -> eventService.updateEvent(publicEvent.getId(), validEventRequestDto));
        assertNotNull(error);
    }

    @Test
    @DisplayName("Update event - end date in past")
    public void testUpdateEvent_endDateInPast() {
        EventRequestDTO pastUpdateDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Updated Event")
                .description("Updated description")
                .maxParticipants(200)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        when(eventRepository.findById(eq(publicEvent.getId()))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.updateEvent(publicEvent.getId(), pastUpdateDto));
        assertEquals("Event must be scheduled in the future", error.getMessage());
    }

    @Test
    @DisplayName("Update event - start date after end date")
    public void testUpdateEvent_startDateAfterEndDate() {
        EventRequestDTO invalidDateUpdateDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Updated Event")
                .description("Updated description")
                .maxParticipants(200)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        when(eventRepository.findById(eq(publicEvent.getId()))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.updateEvent(publicEvent.getId(), invalidDateUpdateDto));
        assertEquals("Start date must be before end date", error.getMessage());
    }

    @Test
    @DisplayName("Update event - same date with start time after end time")
    public void testUpdateEvent_sameDate_startTimeAfterEndTime() {
        EventRequestDTO invalidTimeUpdateDto = EventRequestDTO.builder()
                .eventTypeId(1L)
                .name("Updated Event")
                .description("Updated description")
                .maxParticipants(200)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(9, 0))
                .location(validEventRequestDto.getLocation())
                .build();

        when(eventRepository.findById(eq(publicEvent.getId()))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.updateEvent(publicEvent.getId(), invalidTimeUpdateDto));
        assertEquals("Start time must be before end time", error.getMessage());
    }

    @Test
    @DisplayName("Get event agenda - success")
    public void testGetEventAgenda_success() {
        setupAgendaTestData();

        Long eventId = publicEvent.getId();
        List<Activity> activities = List.of(activity1, activity2);

        when(activityRepository.findByEventIdOrderByStartTimeAsc(eq(eventId))).thenReturn(activities);

        List<ActivityResponseDTO> result = eventService.getEventAgenda(eventId);

        assertEquals(2, result.size());
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(activityDto1);
        assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(activityDto2);
    }

    @Test
    @DisplayName("Get event agenda - empty agenda")
    public void testGetEventAgenda_empty() {
        Long eventId = publicEvent.getId();

        when(activityRepository.findByEventIdOrderByStartTimeAsc(eq(eventId))).thenReturn(List.of());

        List<ActivityResponseDTO> result = eventService.getEventAgenda(eventId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Add activity to agenda - success")
    public void testAddActivityToAgenda_success() {
        setupAgendaTestData();

        Long eventId = publicEvent.getId();
        Activity newActivity = new Activity(3L, "Workshop", "Hands-on coding",
                LocalDateTime.of(2025, 1, 1, 14, 0),
                LocalDateTime.of(2025, 1, 1, 16, 0),
                "Location",
                publicEvent
        );
        ActivityResponseDTO newActivityResponseDto = new ActivityResponseDTO(newActivity.getId(), newActivity.getName(), newActivity.getDescription(), newActivity.getStartTime(), newActivity.getEndTime(), newActivity.getLocation());

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());
        when(activityMapper.toActivity(eq(activityRequestDto))).thenReturn(newActivity);
        when(activityRepository.save(any(Activity.class))).thenReturn(newActivity);
        when(activityMapper.toDTO(newActivity)).thenReturn(newActivityResponseDto);

        ActivityResponseDTO result = eventService.addActivityToAgenda(eventId, activityRequestDto);

        assertThat(result).usingRecursiveComparison().isEqualTo(newActivityResponseDto);
        verify(activityRepository).save(any(Activity.class));
        verify(notificationService).sendEventUpdateNotificationToAllUsers(eq(publicEvent));
    }

    @Test
    @DisplayName("Add activity to agenda - event not found")
    public void testAddActivityToAgenda_eventNotFound() {
        setupAgendaTestData();
        Long eventId = 999L;

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.empty());

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> eventService.addActivityToAgenda(eventId, activityRequestDto));
        assertEquals("Event not found", error.getMessage());
    }

    @Test
    @DisplayName("Add activity to agenda - unauthorized")
    public void testAddActivityToAgenda_unauthorized() {
        setupAgendaTestData();
        Long eventId = publicEvent.getId();

        when(eventRepository.findById(eq(eventId))).thenReturn(Optional.of(publicEvent));
        when(currentUserService.getCurrentUserId()).thenReturn(regularUser.getId()); // Different user

        UnauthorizedError error = assertThrows(UnauthorizedError.class,
                () -> eventService.addActivityToAgenda(eventId, activityRequestDto));
        assertNotNull(error);
    }

    @Test
    @DisplayName("Remove activity from agenda - success")
    public void testRemoveActivityFromAgenda_success() {
        setupAgendaTestData();
        Long eventId = publicEvent.getId();
        Long activityId = activity1.getId();

        when(activityRepository.findById(eq(activityId))).thenReturn(Optional.of(activity1));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());

        assertDoesNotThrow(() -> eventService.removeActivityFromAgenda(eventId, activityId));

        verify(activityRepository).delete(eq(activity1));
        verify(notificationService).sendEventUpdateNotificationToAllUsers(eq(publicEvent));
    }

    @Test
    @DisplayName("Remove activity from agenda - activity not found")
    public void testRemoveActivityFromAgenda_activityNotFound() {
        Long eventId = publicEvent.getId();
        Long activityId = 999L;

        when(activityRepository.findById(eq(activityId))).thenReturn(Optional.empty());

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> eventService.removeActivityFromAgenda(eventId, activityId));
        assertEquals("Activity not found", error.getMessage());
    }

    @Test
    @DisplayName("Remove activity from agenda - invalid activity")
    public void testRemoveActivityFromAgenda_invalidActivity() {
        setupAgendaTestData();
        Long eventId = privateEvent.getId(); // Different event
        Long activityId = activity1.getId(); // Activity belongs to publicEvent

        when(activityRepository.findById(eq(activityId))).thenReturn(Optional.of(activity1));

        InvalidRequestError error = assertThrows(InvalidRequestError.class,
                () -> eventService.removeActivityFromAgenda(eventId, activityId));
        assertEquals("Invalid activity", error.getMessage());
    }

    @Test
    @DisplayName("Remove activity from agenda - unauthorized")
    public void testRemoveActivityFromAgenda_unauthorized() {
        setupAgendaTestData();
        Long eventId = publicEvent.getId();
        Long activityId = activity1.getId();

        when(activityRepository.findById(eq(activityId))).thenReturn(Optional.of(activity1));
        when(currentUserService.getCurrentUserId()).thenReturn(regularUser.getId()); // Different user

        UnauthorizedError error = assertThrows(UnauthorizedError.class,
                () -> eventService.removeActivityFromAgenda(eventId, activityId));
        assertNotNull(error);
    }

    @Test
    @DisplayName("Is attending - user is attending")
    public void testIsAttending_userAttending() {
        Long eventId = publicEvent.getId();
        Long userId = regularUser.getId();
        EventAttendanceId attendanceId = new EventAttendanceId(userId, eventId);

        when(eventAttendanceRepository.existsById(eq(attendanceId))).thenReturn(true);

        Boolean result = eventService.isAttending(eventId, userId);
        assertTrue(result);
    }

    @Test
    @DisplayName("Is attending - user not attending")
    public void testIsAttending_userNotAttending() {
        Long eventId = publicEvent.getId();
        Long userId = regularUser.getId();
        EventAttendanceId attendanceId = new EventAttendanceId(userId, eventId);

        when(eventAttendanceRepository.existsById(eq(attendanceId))).thenReturn(false);

        Boolean result = eventService.isAttending(eventId, userId);
        assertFalse(result);
    }
}
