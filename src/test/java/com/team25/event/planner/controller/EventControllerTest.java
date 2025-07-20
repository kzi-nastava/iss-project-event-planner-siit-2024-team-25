package com.team25.event.planner.controller;

import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.model.PrivacyType;
import com.team25.event.planner.user.dto.LoginRequestDTO;
import com.team25.event.planner.user.dto.LoginResponseDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:event-controller-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken = "";

    private void login(String email, String password) {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setEmail(email);
        req.setPassword(password);
        ResponseEntity<LoginResponseDTO> res = restTemplate.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(req, jsonHeadersWithoutAuth()),
                LoginResponseDTO.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        jwtToken = res.getBody().getJwt();
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = jsonHeadersWithoutAuth();
        h.setBearerAuth(jwtToken);
        return h;
    }

    private static HttpHeaders jsonHeadersWithoutAuth() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/events – happy path (organizer)")
    void createEvent_ok() {
        login("organizer@example.com", "password1");

        EventRequestDTO reqBody = EventRequestDTO.builder()
                .name("JUnit Conference")
                .description("Testing all the things")
                .maxParticipants(100)
                .eventTypeId(1L) // Conference
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusMonths(1))
                .startTime(LocalTime.of(9, 0))
                .endDate(LocalDate.now().plusMonths(1))
                .endTime(LocalTime.of(17, 0))
                .location(new LocationRequestDTO("Bosnia & Herzegovina", "Sarajevo", "Skenderija"))
                .build();

        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events",
                HttpMethod.POST,
                new HttpEntity<>(reqBody, jsonHeaders()),
                EventResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long newEventId = res.getBody().getId();

        // Fetch created event to confirm persistence
        ResponseEntity<EventResponseDTO> getRes = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                EventResponseDTO.class,
                newEventId
        );

        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody().getName()).isEqualTo("JUnit Conference");
        assertThat(getRes.getBody().getMaxParticipants()).isEqualTo(100);
    }


    @Test
    @Order(2)
    @DisplayName("POST /api/events – forbidden for regular user")
    void createEvent_forbidden() {
        login("jana@example.com", "password1");

        EventRequestDTO body = EventRequestDTO.builder()
                .name("Should fail")
                .maxParticipants(10)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(10))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .location(new LocationRequestDTO("Bosnia & Herzegovina", "Banja Luka", "Patre"))
                .build();

        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                EventResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/events/{id} – organizer can view")
    void getEvent_organizerOk() {
        login("organizer@example.com", "password1");

        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                EventResponseDTO.class,
                2L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getId()).isEqualTo(2L);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/events/{id} – unauthorized user blocked by permission evaluator")
    void getEvent_forbidden() {
        login("jana@example.com", "password1");

        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                EventResponseDTO.class,
                2L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/events/{id} - not-logged in user can view public event")
    void getEvent_publicAnonymousOk() {
        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                EventResponseDTO.class,
                1L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @Order(5)
    @DisplayName("PUT /api/events/{id} – successful update by organizer")
    void updateEvent_ok() {
        login("organizer@example.com", "password1");

        EventRequestDTO patch = EventRequestDTO.builder()
                .name("Tech Conference 2024")
                .maxParticipants(120)
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusWeeks(3))
                .endDate(LocalDate.now().plusWeeks(3))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(18, 0))
                .location(new LocationRequestDTO("Country", "City", "Address"))
                .build();

        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(patch, jsonHeaders()),
                EventResponseDTO.class,
                1L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getMaxParticipants()).isEqualTo(120);

        // Verify update via GET
        ResponseEntity<EventResponseDTO> getRes = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                EventResponseDTO.class,
                1L
        );

        assertThat(getRes.getBody().getMaxParticipants()).isEqualTo(120);
    }

    @Test
    @Order(6)
    @DisplayName("PUT /api/events/{id} – forbidden when not organizer")
    void updateEvent_forbidden() {
        login("jana@example.com", "password1");

        EventRequestDTO patch = EventRequestDTO.builder()
                .name("Should not work")
                .privacyType(PrivacyType.PUBLIC)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.NOON)
                .endTime(LocalTime.NOON.plusHours(1))
                .location(new LocationRequestDTO("Country", "City", "Address"))
                .build();

        ResponseEntity<EventResponseDTO> res = restTemplate.exchange(
                "/api/events/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(patch, jsonHeaders()),
                EventResponseDTO.class,
                1L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/events/{id}/agenda – visible to organizer")
    void getAgenda_ok() {
        login("organizer@example.com", "password1");

        ResponseEntity<ActivityResponseDTO[]> res = restTemplate.exchange(
                "/api/events/{id}/agenda",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                ActivityResponseDTO[].class,
                1L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).hasSize(1);
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/events/{id}/agenda – add activity")
    void addActivity_ok() {
        login("organizer@example.com", "password1");

        ActivityRequestDTO dto = ActivityRequestDTO.builder()
                .name("Networking")
                .description("Coffee + chat")
                .startTime(LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.of(12, 30)))
                .endTime(LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.of(13, 30)))
                .location("Lobby")
                .build();

        ResponseEntity<ActivityResponseDTO> res = restTemplate.exchange(
                "/api/events/{id}/agenda",
                HttpMethod.POST,
                new HttpEntity<>(dto, jsonHeaders()),
                ActivityResponseDTO.class,
                1L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().getName()).isEqualTo("Networking");

        ResponseEntity<ActivityResponseDTO[]> getRes = restTemplate.exchange(
                "/api/events/{id}/agenda",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                ActivityResponseDTO[].class,
                1L
        );

        assertThat(getRes.getBody()).anyMatch(a -> a.getId().equals(res.getBody().getId()));
    }

    @Test
    @Order(9)
    @DisplayName("DELETE /api/events/{eventId}/agenda/{activityId} – organizer removes activity")
    void removeActivity_ok() {
        login("organizer@example.com", "password1");

        ResponseEntity<Void> res = restTemplate.exchange(
                "/api/events/{eventId}/agenda/{activityId}",
                HttpMethod.DELETE,
                new HttpEntity<>(jsonHeaders()),
                Void.class,
                1L, 10L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(10)
    @DisplayName("POST /api/events/{eventId}/join – regular user joins open event")
    void joinEvent_ok() {
        login("jana@example.com", "password1");

        JoinEventRequestDTO dto = new JoinEventRequestDTO(2L); // user id = 2

        ResponseEntity<EventPreviewResponseDTO> res = restTemplate.exchange(
                "/api/events/{eventId}/join",
                HttpMethod.POST,
                new HttpEntity<>(dto, jsonHeaders()),
                EventPreviewResponseDTO.class,
                1L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @Order(11)
    @DisplayName("GET /api/events/{eventId}/attending/{userId} – isAttending flag")
    void isAttending_true() {
        login("jana@example.com", "password1");

        ResponseEntity<Boolean> res = restTemplate.exchange(
                "/api/events/{eventId}/attending/{userId}",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                Boolean.class,
                1L, 2L
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isTrue();
    }

    @Test
    @Order(12)
    @DisplayName("GET /api/events/ – organizer’s own events")
    void getMyEvents_ok() {
        login("organizer@example.com", "password1");

        ResponseEntity<PagedResponse> res = restTemplate.exchange(
                "/api/events/",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                PagedResponse.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().totalElements).isGreaterThan(0);
    }

    @Test
    @Order(13)
    @DisplayName("GET /api/events/all – public endpoint")
    void getAllEvents_ok() {
        ResponseEntity<PagedResponse> res = restTemplate.exchange(
                "/api/events/all",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeadersWithoutAuth()), // no auth needed
                PagedResponse.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().content).isNotEmpty();
    }

    @Test
    @Order(14)
    @DisplayName("GET /api/events/top – works with or without login")
    void getTopEvents_ok() {
        ResponseEntity<PagedResponse> res = restTemplate.exchange(
                "/api/events/top",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeadersWithoutAuth()),
                PagedResponse.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // simple typed record to deserialize Spring’s default PageImpl JSON
    private record PagedResponse(List<?> content,
                                 int number,
                                 int size,
                                 long totalElements) { }
}
