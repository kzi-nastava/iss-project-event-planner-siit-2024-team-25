package com.team25.event.planner.controller;

import com.team25.event.planner.common.dto.ErrorResponseDTO;
import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.dto.OfferingCategoryPreviewDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.user.dto.LoginRequestDTO;
import com.team25.event.planner.user.dto.LoginResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.ErrorResponse;

import java.io.Console;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:budget-plan-controller-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BudgetPlanControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

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
    @DisplayName("POST api/budget-items/ - happy path (organizer)")
    void testCreateBudgetItem_ok(){
        login("organizer@example.com", "password1");

        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(2L)
                .offeringCategoryId(2L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );

        // returns created body
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isEqualTo(300.0);
        assertThat(res.getBody().getEventId()).isEqualTo(2L);
        assertThat(res.getBody().getOfferingCategoryId()).isEqualTo(2L);
    }

    @Test
    @Order(2)
    @DisplayName("POST api/budget-items/ - Invalid test create budget item as unauthenticated user - unauthorized")
    void testCreateBudgetItem_unauthenticatedUser(){
        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeadersWithoutAuth()),
                BudgetItemResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();
    }

    @Test
    @Order(3)
    @DisplayName("POST api/budget-items/ - Invalid test create budget item as owner - forbidden")
    void testCreateBudgetItem_owner(){
        login("owner@example.com", "password1");
        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();
    }

    @Test
    @Order(4)
    @DisplayName("POST api/budget-items/ - Invalid test create budget item as admin - forbidden")
    void testCreateBudgetItem_admin(){
        login("admin@example.com", "password1");
        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();
    }

    @Test
    @Order(5)
    @DisplayName("POST api/budget-items/ - Invalid test create budget item as regular user - forbidden")
    void testCreateBudgetItem_regularUser(){
        login("regular@example.com", "password1");
        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();
    }

    @Test
    @Order(6)
    @DisplayName("GET api/budget-items/?eventId=1/ -  Valid test organizer can only view budget items for events that they own")
    void testGetBudgetItemsByEvent_organizerOk(){
        login("organizer@example.com", "password1");

        ResponseEntity<List<BudgetItemResponseDTO>> response = restTemplate.exchange(
                "/api/budget-items/?eventId=1",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                new ParameterizedTypeReference<List<BudgetItemResponseDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThan(0);
    }

    @Test
    @Order(7)
    @DisplayName("GET api/budget-items/?eventId=1 - Invalid test organizer can not view budget items for events that they did not created")
    void testGetBudgetItemsByEvent_organizerFalse(){
        login("organizer2@example.com", "password1");

        ResponseEntity<List<BudgetItemResponseDTO>> response = restTemplate.exchange(
                "/api/budget-items/?eventId=1",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                new ParameterizedTypeReference<List<BudgetItemResponseDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(0);
    }

    @Test
    @Order(8)
    @DisplayName("POST api/budget-items/ - Invalid test when offering category does not exist")
    void testCreateBudgetItem_offeringCategoryNotExist(){
        login("organizer@example.com", "password1");

        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(1L)
                .offeringCategoryId(10L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();
    }

    @Test
    @Order(9)
    @DisplayName("POST api/budget-items/ - Invalid test when event does not exist")
    void testCreateBudgetItem_eventNotExist(){
        login("organizer@example.com", "password1");

        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(300.0)
                .eventId(10L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();
    }

    @Test
    @Order(10)
    @DisplayName("POST api/budget-items/ - Invalid test with null budget")
    void testCreateBudgetItem_nullBudget() {
        login("organizer@example.com", "password1");
        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(null)
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    @Order(11)
    @DisplayName("PUT api/budget-items/1 - Invalid test when budget less than 0")
    void testUpdateBudgetItem_invalidBudget(){
        login("organizer@example.com", "password1");

        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(-12.0) // less than zero
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/1",
                HttpMethod.PUT,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );
        // occurs error
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getBudget()).isNull();
        assertThat(res.getBody().getEventId()).isNull();
        assertThat(res.getBody().getOfferingCategoryId()).isNull();

    }

    @Test
    @Order(12)
    @DisplayName("PUT api/budget-items/1 - Valid update budget item")
    void testUpdateBudgetItem_ok(){
        login("organizer@example.com", "password1");

        BudgetItemRequestDTO requestDTO = BudgetItemRequestDTO.builder()
                .budget(400.0)
                .eventId(1L)
                .offeringCategoryId(1L)
                .build();

        ResponseEntity<BudgetItemResponseDTO> res = restTemplate.exchange(
                "/api/budget-items/1",
                HttpMethod.PUT,
                new HttpEntity<>(requestDTO, jsonHeaders()),
                BudgetItemResponseDTO.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(res.getBody()).isNotNull();
    }


    @Test
    @Order(13)
    @DisplayName("GET api/event-types/10/offering-categories - Invalid fetching offering category when event type does not exist")
    void testGetAvailableOfferingCategoryByEventType(){
        login("organizer@example.com", "password1");
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/event-types/10/offering-categories",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("Event type not found");
    }

    @Test
    @Order(14)
    @DisplayName("GET api/event-types/1/offering-categories - Valid fetching offering category by event type")
    void testGetAvailableOfferingCategoryById_ok(){
        login("organizer@example.com", "password1");
        ResponseEntity<List<OfferingCategoryPreviewDTO>> res = restTemplate.exchange(
                "/api/event-types/1/offering-categories",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                new ParameterizedTypeReference<List<OfferingCategoryPreviewDTO>>() {}
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @Order(15)
    @DisplayName("GET api/budget-items/2/is-suitable?eventId=1 - Valid offering category can be added to budget items")
    void testIsOfferingCategorySuitable_ok(){
        login("organizer@example.com", "password1");
        ResponseEntity<Boolean> res = restTemplate.exchange(
                "/api/budget-items/2/is-suitable?eventId=1",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                Boolean.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).isTrue();
    }

    @Test
    @Order(16)
    @DisplayName("GET api/budget-items/1/is-suitable?eventId=1 - Invalid offering category has already added to budget items")
    void testIsOfferingCategorySuitable_invalid(){
        login("organizer@example.com", "password1");
        ResponseEntity<Boolean> res = restTemplate.exchange(
                "/api/budget-items/1/is-suitable?eventId=1",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                Boolean.class
        );
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).isFalse();
    }

    @Test
    @Order(17)
    @DisplayName("DELETE api/budget-items/2 - Valid deleted budget item")
    void testDeleteBudgetItem_deleteOk(){
        login("organizer@example.com", "password1");
        ResponseEntity<?> res = restTemplate.exchange(
                "/api/budget-items/2",
                HttpMethod.DELETE,
                new HttpEntity<>(jsonHeaders()),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    @Order(18)
    @DisplayName("DELETE api/budget-items/10 - Invalid delete budget item when budget item does not exist")
    void testDeleteBudgetItem_noFound(){
        login("organizer@example.com", "password1");
        ResponseEntity<?> res = restTemplate.exchange(
                "/api/budget-items/10",
                HttpMethod.DELETE,
                new HttpEntity<>(jsonHeaders()),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(19)
    @DisplayName("DELETE api/budget-items/1 - Invalid delete budget item when budget item belongs to purchase")
    void testDeleteBudgetItem_badRequest(){
        login("organizer@example.com", "password1");
        ResponseEntity<?> res = restTemplate.exchange(
                "/api/budget-items/1",
                HttpMethod.DELETE,
                new HttpEntity<>(jsonHeaders()),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
