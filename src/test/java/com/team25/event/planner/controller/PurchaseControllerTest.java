package com.team25.event.planner.controller;

import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceResponseDTO;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import com.team25.event.planner.user.dto.LoginRequestDTO;
import com.team25.event.planner.user.dto.LoginResponseDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:purchase-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PurchaseControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    public Event event;
    public Purchase purchase;
    public Service service;
    public PurchaseServiceRequestDTO purchaseServiceRequestDTO;
    private String jwtToken;

    private void login(String email, String password) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);

        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginRequestDTO, getJsonHeaders()),
                LoginResponseDTO.class
        );
        jwtToken = response.getBody().getJwt();
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @BeforeEach
    public void setup(){
        event = eventRepository.findById(1L).get();
        event.setStartDate(event.getStartDate().plusYears(1));
        event.setEndDate(event.getEndDate().plusYears(1));
        eventRepository.save(event);

        service = serviceRepository.findById(1L).get();

        purchaseServiceRequestDTO = new PurchaseServiceRequestDTO();
        purchaseServiceRequestDTO.setStartDate(event.getStartDate());
        purchaseServiceRequestDTO.setEndDate(event.getEndDate());
        purchaseServiceRequestDTO.setStartTime(event.getStartTime());
        purchaseServiceRequestDTO.setEndTime(event.getStartTime().plusHours(service.getDuration()));
        purchaseServiceRequestDTO.setPrice(service.getPrice()-service.getDiscount()/100*service.getPrice());

        purchase = new Purchase(1L, new Money(purchaseServiceRequestDTO.getPrice()), purchaseServiceRequestDTO.getStartDate(),purchaseServiceRequestDTO.getStartTime(), purchaseServiceRequestDTO.getEndDate(), purchaseServiceRequestDTO.getEndTime(), event, service, null);
    }

    @Test
    @Order(1)
    @DisplayName("Valid test purchase service POST request to endpoint - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceValid() {
        login("account1@example.com", "password1");
        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isEqualTo(1);
        assertThat(response.getBody().getServiceId()).isEqualTo(1);
        assertThat(response.getBody().getStartDate()).isEqualTo(purchase.getStartDate());
        assertThat(response.getBody().getEndDate()).isEqualTo(purchase.getEndDate());
        assertThat(response.getBody().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
        assertThat(response.getBody().getStartTime()).isEqualTo(purchase.getStartTime());
        assertThat(response.getBody().getEndTime()).isEqualTo(purchase.getEndTime());
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Invalid test purchase service POST request as unauthenticated user to endpoint - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceUnauthenticatedUser() {
        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(3)
    @DisplayName("Invalid test purchase service POST request logged as owner to endpoint - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceWhenLoggedAsOwner() {
        login("marko.p@example.com", "password1");
        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(4)
    @DisplayName("Invalid test purchase service POST request logged as admin to endpoint - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceWhenLoggedAsAdmin() {
        login("marko@example.com", "password1");
        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(5)
    @DisplayName("Invalid test purchase service POST request logged as regular user to endpoint - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceWhenLoggedAsRegularUser() {
        login("ana@example.com", "password1");
        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(6)
    @DisplayName("Invalid test purchase service POST request to endpoint when service is not available - /api/purchase/event/1/service/4")
    public void testPostPurchaseServiceWhenServiceIsNotAvailable() {
        login("account1@example.com", "password1");

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/4",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.resolve(422));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(7)
    @DisplayName("Invalid test purchase service POST request to endpoint when service is not available in this period - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceWhenServiceIsNotAvailableInThisPeriod() {
        login("account1@example.com", "password1");

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.resolve(422));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(8)
    @DisplayName("Invalid test purchase service POST request to endpoint when offering category is not suitable for event - /api/purchase/event/1/service/2")
    public void testPostPurchaseServiceWhenServiceCategoryIsNotSuitableForEvent() {
        login("account1@example.com", "password1");

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/2",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.resolve(422));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(9)
    @DisplayName("Invalid test purchase service POST request to endpoint when purchase request has invalid data - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceWhenPurchaseRequestHasInvalidData() {
        login("account1@example.com", "password1");

        purchaseServiceRequestDTO.setStartTime(purchaseServiceRequestDTO.getEndTime().plusMinutes(1));
        purchaseServiceRequestDTO.setEndTime(purchaseServiceRequestDTO.getStartTime().plusHours(service.getDuration()-1));

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.resolve(422));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(10)
    @DisplayName("Invalid test purchase service POST request to endpoint when not enough budget plan money for the product - /api/purchase/event/1/service/1")
    public void testPostPurchaseServiceWhenNotEnoughBudgetPlanMoneyForService() {
        login("account1@example.com", "password1");

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.resolve(422));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    @Test
    @Order(11)
    @DisplayName("Invalid test purchase service POST request to endpoint when event does not exist - /api/purchase/event/25/service/1")
    public void testPostPurchaseServiceWhenEventDoesNotExist() {
        login("account1@example.com", "password1");

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/25/service/1",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }

    //test when service does not exist
    @Test
    @Order(12)
    @DisplayName("Invalid test purchase service POST request to endpoint when service does not exist - /api/purchase/event/1/service/25")
    public void testPostPurchaseServiceWhenServiceDoesNotExist() {
        login("account1@example.com", "password1");

        HttpHeaders headers = this.getJsonHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PurchaseServiceRequestDTO> requestEntity = new HttpEntity<>(purchaseServiceRequestDTO, headers);

        ResponseEntity<PurchaseServiceResponseDTO> response = restTemplate.exchange(
                "/api/purchase/event/1/service/25",
                HttpMethod.POST,
                requestEntity,
                PurchaseServiceResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEventId()).isNull();
        assertThat(response.getBody().getServiceId()).isNull();
        assertThat(response.getBody().getStartDate()).isNull();
        assertThat(response.getBody().getEndDate()).isNull();
        assertThat(response.getBody().getPrice()).isNull();
        assertThat(response.getBody().getStartTime()).isNull();
        assertThat(response.getBody().getEndTime()).isNull();
        assertThat(response.getBody().getId()).isNull();
    }
}
