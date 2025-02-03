    package com.team25.event.planner.service;

    import com.team25.event.planner.common.exception.InvalidRequestError;
    import com.team25.event.planner.common.model.Location;
    import com.team25.event.planner.email.service.EmailService;
    import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
    import com.team25.event.planner.event.mapper.PurchaseMapper;
    import com.team25.event.planner.event.model.*;
    import com.team25.event.planner.event.repository.BudgetItemRepository;
    import com.team25.event.planner.event.repository.EventRepository;
    import com.team25.event.planner.event.repository.PurchaseRepository;
    import com.team25.event.planner.event.service.PurchaseService;
    import com.team25.event.planner.event.specification.PurchaseSpecification;
    import com.team25.event.planner.offering.common.model.OfferingCategory;
    import com.team25.event.planner.offering.common.model.OfferingCategoryType;
    import com.team25.event.planner.offering.common.model.OfferingType;
    import com.team25.event.planner.offering.service.mapper.ServiceMapper;
    import com.team25.event.planner.offering.service.model.ReservationType;
    import com.team25.event.planner.offering.service.model.Service;
    import com.team25.event.planner.offering.service.repository.ServiceRepository;
    import com.team25.event.planner.user.model.*;
    import org.assertj.core.api.Assertions;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.*;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.time.Instant;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.ArrayList;
    import java.util.Optional;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;

    import static org.junit.jupiter.api.Assertions.*;

    @ExtendWith(MockitoExtension.class)
    public class PurchaseServiceTest {

        @Mock
        private ServiceMapper serviceMapper;

        @Mock
        private PurchaseSpecification purchaseSpecification;

        @Mock
        private ServiceRepository serviceRepository;

        @Mock
        private EventRepository eventRepository;

        @Mock
        private PurchaseRepository purchaseRepository;

        @Mock
        private PurchaseMapper purchaseMapper;

        @Mock
        private BudgetItemRepository budgetItemRepository;

        @Mock
        private EmailService emailService;

        @Captor
        private ArgumentCaptor<Purchase> purchaseCaptor;

        @Captor
        private ArgumentCaptor<BudgetItem> budgetItemCaptor;

        @Spy
        @InjectMocks
        private PurchaseService purchaseService;


        public Event event;
        public Service service;
        public PurchaseServiceRequestDTO requestDTO;
        public Purchase purchase;


        @BeforeEach
        public void setup() {

            Account account1 = new Account(null, "account1@example.com", "password1", AccountStatus.ACTIVE, null, null);
            Location location = new Location("Serbia", "Belgrade", "Bulevar Kralja Aleksandra 10", 44.0, 20.0);
            PhoneNumber phone = new PhoneNumber("+381601234567");
            EventOrganizer eventOrganizer = new EventOrganizer(
                    null,
                    "John",
                    "Doe",
                    "https://example.com/johndoe.jpg",
                    UserRole.EVENT_ORGANIZER,
                    account1,
                    null,
                    null,
                    null,
                    null,
                    location,
                    phone
            );

            Owner owner = new Owner(
                    null,
                    "John",
                    "Doe",
                    "https://example.com/johndoe.jpg",
                    UserRole.EVENT_ORGANIZER,
                    account1,
                    null,
                    null,
                    null,
                    null,
                    "Company",
                    location,
                    phone,
                    "Description",
                    null
            );

            OfferingCategory offeringCategory = new OfferingCategory(1L,"Conference Catering category", "", OfferingCategoryType.ACCEPTED);
            ArrayList<OfferingCategory> offeringCategories = new ArrayList<>();
            offeringCategories.add(offeringCategory);
            EventType conferenceType = new EventType(1L,"Description", "Conference", true, offeringCategories);

            LocalDate eventStartDate = LocalDate.now().plusYears(1).withDayOfMonth(1);
            LocalDate eventEndDate = LocalDate.now().plusYears(1).withDayOfMonth(1);
            LocalTime eventStartTime = LocalTime.of(9,0);
            LocalTime eventEndTime = LocalTime.of(18,0);

            event = new Event(1L, conferenceType, "Tech Conference 2024", "A conference about technology", 200, PrivacyType.PUBLIC,
                    eventStartDate, eventEndDate, eventStartTime, eventEndTime, location, eventOrganizer, Instant.now(),null, null, null);

            service = new Service();
            service.setId(1L);
            service.setName("Conference Catering");
            service.setDescription("Premium catering service for tech events");
            service.setPrice(5000.0);
            service.setDiscount(0);
            service.setImages(null);
            service.setVisible(true);
            service.setAvailable(true);
            service.setSpecifics("Includes food, drinks, and coffee breaks");
            service.setReservationType(ReservationType.AUTOMATIC);
            service.setDuration(3);
            service.setReservationDeadline(24);
            service.setCancellationDeadline(24);
            service.setOfferingCategory(offeringCategory);
            service.setOwner(owner);
            service.setEventTypes(null);
            service.setStatus(OfferingType.ACCEPTED);

            requestDTO = new PurchaseServiceRequestDTO();
            requestDTO.setStartDate(event.getStartDate());
            requestDTO.setEndDate(event.getEndDate());
            requestDTO.setStartTime(event.getStartTime());
            requestDTO.setEndTime(event.getStartTime().plusHours(service.getDuration()));
            requestDTO.setPrice(service.getPrice()-service.getDiscount()/100*service.getPrice());

            purchase = new Purchase(1L, new Money(requestDTO.getPrice()), requestDTO.getStartDate(),requestDTO.getStartTime(), requestDTO.getEndDate(), requestDTO.getEndTime(), event, service, null);

        }

        @Test
        @DisplayName("Valid purchase request when service has duration")
        public void testPurchaseServiceWithValidRequestWhenServiceHasDuration(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(-1.0).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(1)).save(budgetItemCaptor.capture());
            Assertions.assertThat(budgetItemCaptor.getValue().getEvent().getId()).isEqualTo(purchase.getEvent().getId());
            Assertions.assertThat(budgetItemCaptor.getValue().getMoney().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(budgetItemCaptor.getValue().getOfferingCategory().getId()).isEqualTo(purchase.getOffering().getOfferingCategory().getId());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Valid purchase request with min and max arrangement")
        public void testPurchaseServiceWithValidRequestWhenServiceHasArrangement(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(-1.0).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            service.setMinimumArrangement(2);
            service.setMaximumArrangement(5);
            service.setDuration(0);

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(1)).save(budgetItemCaptor.capture());
            Assertions.assertThat(budgetItemCaptor.getValue().getEvent().getId()).isEqualTo(purchase.getEvent().getId());
            Assertions.assertThat(budgetItemCaptor.getValue().getMoney().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(budgetItemCaptor.getValue().getOfferingCategory().getId()).isEqualTo(purchase.getOffering().getOfferingCategory().getId());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Invalid purchase request when service isn't available")
        public void testPurchaseServiceWhenServiceIsNotAvailable(){
            service.setAvailable(false);
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Service is not available.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when service isn't available in this period.")
        public void testPurchaseServiceWhenServiceIsAlreadyPurchased(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(false).when(purchaseService).isServiceAvailable(any(), any());

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Service is not available in this period.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when service category isn't suitable for event.")
        public void testPurchaseServiceWhenServiceCategoryIsNotSuitableForEvent(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            event.getEventType().setOfferingCategories(new ArrayList<>());

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Product category is not suitable for event.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request for past event")
        public void testPurchaseServiceForPastEvent(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            event.setStartDate(LocalDate.now().minusDays(3));
            event.setEndDate(LocalDate.now().minusDays(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request after reservation deadline.")
        public void testPurchaseServiceAfterReservationDeadline(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setStartDate(LocalDate.now());
            purchase.setStartTime(LocalTime.now().minusHours(4));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase start date is before event start date")
        public void testPurchaseServiceWhenPurchaseStartDateIsBeforeEventStartDate(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setStartDate(event.getStartDate().minusDays(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase start date is equal to event start date but purchase start time is before event start time")
        public void testPurchaseServiceWhenPurchaseStartTimeIsBeforeEventStartTime(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setStartTime(event.getStartTime().minusHours(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase start date is after event start date")
        public void testPurchaseServiceWhenPurchaseStartDateIsAfterEventStartDate(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setStartDate(event.getEndDate().plusDays(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase start date is equal to event end date but purchase start time is after event end time")
        public void testPurchaseServiceWhenPurchaseStartTimeIsAfterEventEndTime(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setStartTime(event.getEndTime().plusHours(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase end date is before event start date")
        public void testPurchaseServiceWhenPurchaseEndDateIsBeforeEventStartDate(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setStartDate(event.getStartDate().minusDays(1));
            purchase.setEndDate(event.getStartDate().minusDays(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase end date is equal to event start date but purchase end time is before event start time")
        public void testPurchaseServiceWhenPurchaseEndTimeIsBeforeEventStartTime(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setEndTime(event.getStartTime().minusHours(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase end date is after event end date")
        public void testPurchaseServiceWhenPurchaseEndDateIsAfterEventEndDate(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setEndDate(event.getEndDate().plusDays(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase end date is equal to event end date but purchase end time is after event end time")
        public void testPurchaseServiceWhenPurchaseEndTimeIsAfterEventEndTime(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            purchase.setEndTime(event.getEndTime().plusHours(1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase duration is greater than service duration")
        public void testPurchaseServiceWhenPurchaseDurationIsGreaterThanServiceDuration(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());


            requestDTO.setEndTime(requestDTO.getStartTime().plusHours(service.getDuration()+1));
            purchase.setEndTime(purchase.getStartTime().plusHours(service.getDuration()+1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase duration is less than service duration")
        public void testPurchaseServiceWhenPurchaseDurationIsLessThanServiceDuration(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            requestDTO.setEndTime(requestDTO.getStartTime().plusHours(service.getDuration()-1));
            purchase.setEndTime(purchase.getStartTime().plusHours(service.getDuration()-1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase duration is greater than service maximum arrangement")
        public void testPurchaseServiceWhenPurchaseDurationIsGreaterThanServiceMaximumArrangement(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            service.setDuration(0);
            service.setMinimumArrangement(2);
            service.setMaximumArrangement(4);
            requestDTO.setEndTime(requestDTO.getStartTime().plusHours(service.getMaximumArrangement()+1));
            purchase.setEndTime(purchase.getStartTime().plusHours(service.getMaximumArrangement()+1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase duration is less than service minimum arrangement")
        public void testPurchaseServiceWhenPurchaseDurationIsLessThanServiceMinimumArrangement(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            service.setDuration(0);
            service.setMinimumArrangement(2);
            service.setMaximumArrangement(4);
            requestDTO.setEndTime(requestDTO.getStartTime().plusHours(service.getMinimumArrangement()-1));
            purchase.setEndTime(purchase.getStartTime().plusHours(service.getMinimumArrangement()-1));

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Valid purchase request when purchase duration is equal to service minimum arrangement")
        public void testPurchaseServiceWhenPurchaseDurationIsEqualToServiceMinimumArrangement(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(-1.0).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            service.setDuration(0);
            service.setMinimumArrangement(2);
            service.setMaximumArrangement(4);
            requestDTO.setEndTime(requestDTO.getStartTime().plusHours(service.getMinimumArrangement()));
            purchase.setEndTime(purchase.getStartTime().plusHours(service.getMinimumArrangement()));

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(1)).save(budgetItemCaptor.capture());
            Assertions.assertThat(budgetItemCaptor.getValue().getEvent().getId()).isEqualTo(purchase.getEvent().getId());
            Assertions.assertThat(budgetItemCaptor.getValue().getMoney().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(budgetItemCaptor.getValue().getOfferingCategory().getId()).isEqualTo(purchase.getOffering().getOfferingCategory().getId());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Valid purchase request when purchase duration is equal to service maximum arrangement")
        public void testPurchaseServiceWhenPurchaseDurationIsEqualToServiceMaximumArrangement(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(-1.0).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            service.setDuration(0);
            service.setMinimumArrangement(2);
            service.setMaximumArrangement(4);
            requestDTO.setEndTime(requestDTO.getStartTime().plusHours(service.getMaximumArrangement()));
            purchase.setEndTime(purchase.getStartTime().plusHours(service.getMaximumArrangement()));

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(1)).save(budgetItemCaptor.capture());
            Assertions.assertThat(budgetItemCaptor.getValue().getEvent().getId()).isEqualTo(purchase.getEvent().getId());
            Assertions.assertThat(budgetItemCaptor.getValue().getMoney().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(budgetItemCaptor.getValue().getOfferingCategory().getId()).isEqualTo(purchase.getOffering().getOfferingCategory().getId());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase price is less than service price without discount")
        public void testPurchaseServiceWhenPriceIsLessThanServicePrice(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            requestDTO.setPrice(2000);
            service.setPrice(2500);
            service.setDiscount(0);

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase price is greater than service price without discount")
        public void testPurchaseServiceWhenPriceIsGreaterThanServicePrice(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());

            requestDTO.setPrice(5000);
            service.setPrice(2500);
            service.setDiscount(0);

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase price is less than service price with discount")
        public void testPurchaseServiceWhenPriceIsLessThanServicePriceWithDiscount(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());


            requestDTO.setPrice(5000);
            purchase.getPrice().setAmount(requestDTO.getPrice());
            service.setPrice(10000);
            service.setDiscount(20);

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Invalid purchase request when purchase price is greater than service price with discount")
        public void testPurchaseServiceWhenPriceIsGreaterThanServicePriceWithDiscount(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());


            requestDTO.setPrice(10000);
            purchase.getPrice().setAmount(requestDTO.getPrice());
            service.setPrice(10000);
            service.setDiscount(20);

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Purchase request has invalid data.");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Valid purchase request when purchase price is equal to service price with discount")
        public void testPurchaseServiceWhenPriceIsEqualToServicePriceWithDiscount(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(-1.0).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());


            requestDTO.setPrice(8000);
            purchase.getPrice().setAmount(requestDTO.getPrice());
            service.setPrice(10000);
            service.setDiscount(20);

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(1)).save(budgetItemCaptor.capture());
            Assertions.assertThat(budgetItemCaptor.getValue().getEvent().getId()).isEqualTo(purchase.getEvent().getId());
            Assertions.assertThat(budgetItemCaptor.getValue().getMoney().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(budgetItemCaptor.getValue().getOfferingCategory().getId()).isEqualTo(purchase.getOffering().getOfferingCategory().getId());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
        }

        @Test
        @DisplayName("Invalid purchase request when service price is greater than left money")
        public void testPurchaseServiceWhenServicePriceIsGreaterThanLeftMoney(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(1200.0).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            InvalidRequestError exception = assertThrowsExactly(InvalidRequestError.class, () ->
                    purchaseService.purchaseService(requestDTO, event.getId(), service.getId()));

            assertEquals(exception.getMessage(),"Not enough budget plan money for the product");

            verify(purchaseRepository, times(0)).save(purchaseCaptor.capture());
            verify(emailService, times(0)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());
            verify(purchaseMapper, times(0)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Valid purchase request when service price is less than left money")
        public void testPurchaseServiceWhenServicePriceIsLessThanLeftMoney(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(service.getPrice()*2).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
        }

        @Test
        @DisplayName("Valid purchase request when service price is equal to left money")
        public void testPurchaseServiceWhenServicePriceIsEqualToLeftMoney(){
            Mockito.when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
            Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            Mockito.when(purchaseMapper.toPurchase(any(PurchaseServiceRequestDTO.class), any(Event.class), any(Service.class))).thenReturn(purchase);
            Mockito.doReturn(true).when(purchaseService).isServiceAvailable(any(), any());
            Mockito.doReturn(service.getPrice()).when(purchaseService).getLeftMoneyFromBudgetItem(any(), any());

            purchaseService.purchaseService(requestDTO, event.getId(), service.getId());

            verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());
            Assertions.assertThat(purchaseCaptor.getValue().getStartDate()).isEqualTo(purchase.getStartDate());
            Assertions.assertThat(purchaseCaptor.getValue().getStartTime()).isEqualTo(purchase.getStartTime());
            Assertions.assertThat(purchaseCaptor.getValue().getEndDate()).isEqualTo(purchase.getEndDate());
            Assertions.assertThat(purchaseCaptor.getValue().getEndTime()).isEqualTo(purchase.getEndTime());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo(purchase.getPrice().getAmount());
            Assertions.assertThat(purchaseCaptor.getValue().getPrice().getAmount()).isEqualTo((100-service.getDiscount())*service.getPrice()/100);

            verify(emailService, times(1)).sendServicePurchaseConfirmation(purchaseCaptor.capture());
            Assertions.assertThat(purchaseCaptor.getValue().getId()).isEqualTo(purchase.getId());

            verify(budgetItemRepository, times(0)).save(budgetItemCaptor.capture());

            verify(purchaseMapper, times(1)).toServiceResponseDTO(purchaseCaptor.capture());
        }

    }
