package com.team25.event.planner.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.dto.OfferingCategoryPreviewDTO;
import com.team25.event.planner.event.mapper.BudgetItemMapper;
import com.team25.event.planner.event.mapper.OfferingCategoryMapper;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.event.service.EventTypeService;
import com.team25.event.planner.offering.common.controller.PriceListController;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.service.CurrentUserService;
import com.team25.event.planner.event.service.BudgetItemService;
import org.aspectj.weaver.ast.Not;
import org.hibernate.annotations.UpdateTimestamp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BudgetPlanServiceTest {

    @MockitoBean
    private BudgetItemRepository budgetItemRepository;

    @MockitoBean
    private EventRepository eventRepository;

    @MockitoBean
    private BudgetItemMapper budgetItemMapper;
    @MockitoBean
    private OfferingCategoryMapper offeringCategoryMapper;

    @MockitoBean
    private OfferingCategoryRepository offeringCategoryRepository;

    @MockitoBean
    private PurchaseRepository purchaseRepository;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private BudgetItemService budgetItemService;

    // Shared test objects
    private Event event;
    private OfferingCategory offeringCategory;
    private BudgetItem budgetItem;
    private BudgetItemRequestDTO budgetItemRequestDTO;
    private BudgetItemResponseDTO budgetItemResponseDTO;
    private Purchase purchase;
    private Account account;
    private Location location;
    private PhoneNumber phone;
    private EventOrganizer eventOrganizer;
    private EventType eventType;


    @BeforeEach
    public void setUp() {
        account = new Account(1L, "account1@example.com", "password1", AccountStatus.ACTIVE, null, null);

        location = new Location("Serbia", "Belgrade", "Bulevar Kralja Aleksandra 10", 44.0, 20.0);
        phone = new PhoneNumber("+381601234567");
        Money price = new Money(100.0, "EUR");
        eventOrganizer = new EventOrganizer(
                1L, "John", "Doe", "https://example.com/johndoe.jpg", UserRole.EVENT_ORGANIZER,
                account, null, null, null, null, location, phone);

        event = new Event();
        event.setId(1L);
        event.setOrganizer(eventOrganizer);

        offeringCategory = new OfferingCategory();
        offeringCategory.setId(1L);
        offeringCategory.setDescription("Test");
        offeringCategory.setStatus(OfferingCategoryType.ACCEPTED);

        eventType = new EventType(
                1L,
                "Test",
                "Test",
                Boolean.TRUE,
                List.of(offeringCategory));

        budgetItem = new BudgetItem();
        budgetItem.setId(1L);
        budgetItem.setEvent(event);
        budgetItem.setOfferingCategory(offeringCategory);
        budgetItem.setMoney(new Money(100.0, "EUR"));

        budgetItemRequestDTO = BudgetItemRequestDTO.builder()
                .eventId(event.getId())
                .offeringCategoryId(offeringCategory.getId())
                .budget(100.0)
                .build();

        budgetItemResponseDTO = new BudgetItemResponseDTO();
        budgetItemResponseDTO.setId(1L);
        budgetItemResponseDTO.setEventId(event.getId());
        budgetItemResponseDTO.setOfferingCategoryId(offeringCategory.getId());
        budgetItemResponseDTO.setBudget(100.0);

        Offering offering = new Offering();
        offering.setId(1L);
        offering.setOfferingCategory(offeringCategory);
        offering.setPrice(price.getAmount());

        purchase = new Purchase();
        purchase.setId(1L);
        purchase.setStartDate(LocalDate.of(2025, 8, 1));
        purchase.setStartTime(LocalTime.of(10, 0));
        purchase.setEndDate(LocalDate.of(2025, 8, 1));
        purchase.setEndTime(LocalTime.of(12, 0));
        purchase.setPrice(price);
        purchase.setEvent(event);
        purchase.setOffering(offering);
        Review review1 = new Review();
        review1.setId(1L);
        review1.setPurchase(purchase);

        Review review2 = new Review();
        review2.setId(2L);
        review2.setPurchase(purchase);

        purchase.setReviews(List.of(review1, review2));

    }

    @Test
    @DisplayName("Get all budget items when there is no budget items")
    void testGetAllBudgetItems_emptyList() {
        when(budgetItemRepository.findAll()).thenReturn(new ArrayList<>());

        List<BudgetItemResponseDTO> result = budgetItemService.getAllBudgetItems();

        assertTrue(result.isEmpty());

        verify(budgetItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get all budget items when there is one item")
    void testGetAllBudgetItems_oneItem() {
        when(budgetItemRepository.findAll()).thenReturn(List.of(budgetItem));
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        List<BudgetItemResponseDTO> result = budgetItemService.getAllBudgetItems();

        assertEquals(1, result.size());
        assertEquals(budgetItemResponseDTO, result.get(0));

        verify(budgetItemRepository, times(1)).findAll();
        verify(budgetItemMapper, times(1)).toResponseDTO(budgetItem);
    }

    @Test
    @DisplayName("Get all budget items when there is two or more items")
    void testGetAllBudgetItems_nonEmptyList() {
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(1L);
        budgetItem2.setEvent(event);
        budgetItem2.setOfferingCategory(offeringCategory);
        budgetItem2.setMoney(new Money(100.0, "EUR"));

        when(budgetItemRepository.findAll()).thenReturn(List.of(budgetItem, budgetItem2));
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        List<BudgetItemResponseDTO> result = budgetItemService.getAllBudgetItems();

        assertEquals(2, result.size());
        assertEquals(budgetItemResponseDTO, result.getFirst());

        verify(budgetItemRepository, times(1)).findAll();
        verify(budgetItemMapper, times(2)).toResponseDTO(any(BudgetItem.class));
    }

    @Test
    @DisplayName("Get budget items when organizer does not have items")
    void testGetBudgetItemsByEven_empty() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());
        when(budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId()))
                .thenReturn(new ArrayList<>());

        List<BudgetItemResponseDTO> result = budgetItemService.getBudgetItemsByEvent(event.getId());

        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(currentUserService, times(1)).getCurrentUserId();
        verify(budgetItemRepository, times(1)).findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId());
    }

    @Test
    @DisplayName("Get budget items when there is one item by event")
    void testGetBudgetItemsByEvent_success() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());
        when(budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId()))
                .thenReturn(List.of(budgetItem));
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        List<BudgetItemResponseDTO> result = budgetItemService.getBudgetItemsByEvent(event.getId());

        assertEquals(1, result.size());
        assertEquals(budgetItemResponseDTO, result.getFirst());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(currentUserService, times(1)).getCurrentUserId();
        verify(budgetItemRepository, times(1)).findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId());
        verify(budgetItemMapper, times(1)).toResponseDTO(budgetItem);

    }

    @Test
    @DisplayName("Get budget items when there is two or more items by event")
    void testGetBudgetItemsByEvent_successMore() {
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(1L);
        budgetItem2.setEvent(event);
        budgetItem2.setOfferingCategory(offeringCategory);
        budgetItem2.setMoney(new Money(100.0, "EUR"));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(currentUserService.getCurrentUserId()).thenReturn(eventOrganizer.getId());
        when(budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId()))
                .thenReturn(List.of(budgetItem, budgetItem2));
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        List<BudgetItemResponseDTO> result = budgetItemService.getBudgetItemsByEvent(event.getId());

        assertEquals(2, result.size());
        assertEquals(budgetItemResponseDTO, result.get(0));
        assertEquals(budgetItemResponseDTO, result.get(1));

        verify(eventRepository, times(1)).findById(event.getId());
        verify(currentUserService, times(1)).getCurrentUserId();
        verify(budgetItemRepository, times(1)).findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId());
        verify(budgetItemMapper, times(2)).toResponseDTO(any(BudgetItem.class));

    }

    @Test
    @DisplayName("Get budget items when event does not exist")
    void testGetBudgetItemsByEvent_eventNotFound() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundError.class, () -> budgetItemService.getBudgetItemsByEvent(event.getId()));

        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    @DisplayName("Get budget items when organizer does not exist")
    void testGetBudgetItemsByEvent_organizerNotFound() {
        Long organizerId = 100L;
        when(budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), organizerId)).thenReturn(List.of());
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(currentUserService.getCurrentUserId()).thenReturn(organizerId);
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        Assertions.assertTrue(() -> budgetItemService.getBudgetItemsByEvent(event.getId()).isEmpty());

        verify(eventRepository, times(1)).findById(event.getId());
        verify(currentUserService, times(1)).getCurrentUserId();
        verify(budgetItemRepository, times(1)).findByEventIdAndOrganizerId(event.getId(), organizerId);
    }

    @Test
    @DisplayName("Get budget item when organizer try to get budget item from other organizers")
    void testGetBudgetItemsByEvent_budgetItemCreatedByYou() {
        Long otherOrganizer = 2L;
        when(budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), eventOrganizer.getId()))
                .thenReturn(List.of());
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(currentUserService.getCurrentUserId()).thenReturn(otherOrganizer);
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        Assertions.assertTrue(() -> budgetItemService.getBudgetItemsByEvent(event.getId()).isEmpty());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(currentUserService, times(1)).getCurrentUserId();
        verify(budgetItemRepository, times(1)).findByEventIdAndOrganizerId(event.getId(), otherOrganizer);
    }

    @Test
    @DisplayName("Get budget item by id")
    void testGetBudgetItemById_success() {
        when(budgetItemRepository.findById(budgetItem.getId())).thenReturn(Optional.of(budgetItem));
        when(budgetItemMapper.toResponseDTO(any(BudgetItem.class))).thenReturn(budgetItemResponseDTO);

        BudgetItemResponseDTO result = budgetItemService.getBudgetItemById(budgetItem.getId());

        assertEquals(budgetItemResponseDTO, result);
        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
        verify(budgetItemMapper, times(1)).toResponseDTO(budgetItem);

    }

    @Test
    @DisplayName("Get budget item by id when item does not exist")
    void testGetBudgetItemById_notFound() {
        when(budgetItemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundError error = assertThrows(NotFoundError.class, () -> budgetItemService.getBudgetItemById(budgetItem.getId()));
        assertEquals(error.getMessage(), "Budget item not found");
        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
    }

    @Test
    @DisplayName("Is budget item suitable")
    void testIsSuitableByOfferIdAndEventId_success() {
        OfferingCategory offeringCategory2 = new OfferingCategory();
        offeringCategory2.setId(2L);
        offeringCategory2.setDescription("Test2");
        offeringCategory2.setStatus(OfferingCategoryType.ACCEPTED);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(offeringCategory2.getId())).thenReturn(Optional.of(offeringCategory));
        when(budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId())).thenReturn(false);

        assertTrue(budgetItemService.isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId()));
        verify(eventRepository, times(1)).findById(event.getId());
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory2.getId());
        verify(budgetItemRepository, times(1)).isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId());
    }

    @Test
    @DisplayName("Is budget item suitable when event does not exist")
    void testIsSuitableByOfferIdAndEventId_eventNotFound() {
        Long nonExistingEventId = 100L;
        when(eventRepository.findById(nonExistingEventId)).thenReturn(Optional.empty());

        NotFoundError error = assertThrows(NotFoundError.class,
                () -> budgetItemService.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), nonExistingEventId));
        assertEquals(error.getMessage(), "Event not found");
        verify(eventRepository, times(1)).findById(nonExistingEventId);
    }

    @Test
    @DisplayName("Is budget item suitable when offering category does not exist")
    void testIsSuitableByOfferIdAndEventId_categoryNotFound() {
        Long nonExistingOfferingCategoryId = 100L;
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(nonExistingOfferingCategoryId)).thenReturn(Optional.empty());
        NotFoundError error = assertThrows(NotFoundError.class,
                () -> budgetItemService.isSuitableByOfferIdAndNotEventId(nonExistingOfferingCategoryId, event.getId()));
        assertEquals(error.getMessage(), "Offering category not found");
        verify(eventRepository, times(1)).findById(event.getId());
        verify(offeringCategoryRepository, times(1)).findById(nonExistingOfferingCategoryId);
    }

    @Test
    @DisplayName("Is budget suitable when offering category exist, but event does not exist in all budget items")
    void testIsSuitableByOfferIdAndEventId_categoryExist() {
        Event event2 = new Event();
        event2.setId(100L);

        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(2L);
        budgetItem2.setEvent(event2);
        budgetItem2.setOfferingCategory(offeringCategory);
        budgetItem2.setMoney(new Money(100, "EUR"));

        when(eventRepository.findById(event2.getId())).thenReturn(Optional.of(event2));
        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event2.getId()))
                .thenReturn(false);

        assertTrue(budgetItemService.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event2.getId()));
        verify(eventRepository, times(1)).findById(event2.getId());
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(budgetItemRepository, times(1)).isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event2.getId());

    }

    @Test
    @DisplayName("Is budget item suitable when event exist, but offering category does not exist in all budget items")
    void testIsSuitableByOfferIdAndEventId_eventExist() {
        OfferingCategory offeringCategory2 = new OfferingCategory();
        offeringCategory2.setId(2L);
        offeringCategory2.setDescription("Test");
        offeringCategory2.setStatus(OfferingCategoryType.ACCEPTED);

        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(2L);
        budgetItem2.setEvent(event);
        budgetItem2.setOfferingCategory(offeringCategory2);
        budgetItem2.setMoney(new Money(100, "EUR"));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(offeringCategory2.getId())).thenReturn(Optional.of(offeringCategory2));
        when(budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId()))
                .thenReturn(false);

        assertTrue(budgetItemService.isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId()));
        verify(eventRepository, times(1)).findById(event.getId());
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory2.getId());
        verify(budgetItemRepository,times(1)).isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId());
    }

    @Test
    @DisplayName("Is budget item suitable when offering category is not accepted")
    void testIsSuitableByOfferIdAndEventId_categoryNotAccepted() {
        OfferingCategory offeringCategory2 = new OfferingCategory();
        offeringCategory2.setId(2L);
        offeringCategory2.setDescription("Test");
        offeringCategory2.setStatus(OfferingCategoryType.PENDING);

        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(2L);
        budgetItem2.setEvent(event);
        budgetItem2.setOfferingCategory(offeringCategory2);
        budgetItem2.setMoney(new Money(100, "EUR"));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(offeringCategory2.getId())).thenReturn(Optional.of(offeringCategory2));
        when(budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId()))
                .thenReturn(false);

        InvalidRequestError error = assertThrows(InvalidRequestError.class, () ->budgetItemService.isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId()));
        assertEquals(error.getMessage(),"Offering category is not accepted");
        verify(eventRepository, times(1)).findById(event.getId());
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory2.getId());
        verify(budgetItemRepository,times(0)).isSuitableByOfferIdAndNotEventId(offeringCategory2.getId(), event.getId());

    }

    @Test
    @DisplayName("Budget item is not suitable")
    void testIsSuitableByOfferIdAndEventId_notSuitable() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId()))
                .thenReturn(true);

        assertFalse(budgetItemService.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId()));
        verify(eventRepository, times(1)).findById(event.getId());
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(budgetItemRepository,times(1)).isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId());

    }

    @Test
    @DisplayName("Get offering category by event type ID")
    void testGetOfferingCategoriesByEventType_successOne() {
        OfferingCategoryPreviewDTO dto = new OfferingCategoryPreviewDTO();
        dto.setName("Test");
        dto.setId(offeringCategory.getId());

        when(eventTypeRepository.findById(eventType.getId())).thenReturn(Optional.of(eventType));
        when(offeringCategoryMapper.toDTO(offeringCategory)).thenReturn(dto);

        List<OfferingCategoryPreviewDTO> result = eventTypeService.getOfferingCategoryByEventType(eventType.getId());

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());
        verify(eventTypeRepository, times(1)).findById(eventType.getId());
        verify(offeringCategoryMapper, times(1)).toDTO(offeringCategory);
    }

    @Test
    @DisplayName("Get offering categories by event type when event type does not exist")
    void testGetOfferingCategoriesByEventType_eventTypeDoesNotExist() {
        Long nonExistingEventTypeId = 999L;

        when(eventTypeRepository.findById(nonExistingEventTypeId)).thenReturn(Optional.empty());

        NotFoundError exception = assertThrows(
                NotFoundError.class,
                () -> eventTypeService.getOfferingCategoryByEventType(nonExistingEventTypeId)
        );

        assertEquals("Event type not found", exception.getMessage());

        verify(eventTypeRepository, times(1)).findById(nonExistingEventTypeId);
        verifyNoInteractions(offeringCategoryMapper);
    }

    @Test
    @DisplayName("Get offering categories by event type id when categories is empty")
    void testGetOfferingCategoriesByEventType_emptyList() {

        eventType.setOfferingCategories(new ArrayList<>());

        when(eventTypeRepository.findById(eventType.getId())).thenReturn(Optional.of(eventType));

        List<OfferingCategoryPreviewDTO> result = eventTypeService.getOfferingCategoryByEventType(eventType.getId());

        assertTrue(result.isEmpty());
        verify(eventTypeRepository, times(1)).findById(eventType.getId());

    }

    @Test
    @DisplayName("Get offering categories by event type")
    void testGetOfferingCategoriesByEventType_successMore() {
        OfferingCategoryPreviewDTO dto = new OfferingCategoryPreviewDTO();
        dto.setName("Test");
        dto.setId(offeringCategory.getId());

        OfferingCategory offeringCategory2 = new OfferingCategory();
        offeringCategory2.setId(2L);
        offeringCategory2.setDescription("Test");
        offeringCategory2.setStatus(OfferingCategoryType.ACCEPTED);

        OfferingCategory offeringCategory3 = new OfferingCategory();
        offeringCategory3.setId(3L);
        offeringCategory3.setDescription("Test");
        offeringCategory3.setStatus(OfferingCategoryType.ACCEPTED);

        eventType.setOfferingCategories(List.of(offeringCategory,offeringCategory2, offeringCategory3));

        when(eventTypeRepository.findById(eventType.getId())).thenReturn(Optional.of(eventType));
        when(offeringCategoryMapper.toDTO(offeringCategory)).thenReturn(dto);

        List<OfferingCategoryPreviewDTO> result = eventTypeService.getOfferingCategoryByEventType(eventType.getId());

        assertEquals(3, result.size());
        assertEquals(dto, result.getFirst());

        verify(eventTypeRepository, times(1)).findById(eventType.getId());
        verify(offeringCategoryMapper, times(1)).toDTO(offeringCategory);
    }

    @Test
    @DisplayName("Create budget item")
    void testCreateBudgetItem_success() {
        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(budgetItemMapper.toBudgetItem(budgetItemRequestDTO)).thenReturn(budgetItem);
        when(budgetItemRepository.save(budgetItem)).thenReturn(budgetItem);
        when(budgetItemMapper.toResponseDTO(budgetItem)).thenReturn(budgetItemResponseDTO);

        BudgetItemResponseDTO result = budgetItemService.createBudgetItem(budgetItemRequestDTO);

        assertEquals(budgetItemResponseDTO, result);
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(budgetItemMapper, times(1)).toBudgetItem(budgetItemRequestDTO);
        verify(budgetItemRepository, times(1)).save(budgetItem);
        verify(budgetItemMapper, times(1)).toResponseDTO(budgetItem);

    }

    @Test
    @DisplayName("Create budget item when offering category does not exist")
    void testCreateBudgetItem_offeringCategoryNotFound() {
        Long nonExistingOfferingCategory = 100L;
        budgetItemRequestDTO.setOfferingCategoryId(nonExistingOfferingCategory);

        when(offeringCategoryRepository.findById(nonExistingOfferingCategory)).thenReturn(Optional.empty());

        NotFoundError exception = assertThrows(NotFoundError.class, () -> budgetItemService.createBudgetItem(budgetItemRequestDTO));
        assertEquals("Offering category not found", exception.getMessage());
        verify(offeringCategoryRepository, times(1)).findById(nonExistingOfferingCategory);

    }

    @Test
    @DisplayName("Create budget item when event does not exist")
    void testCreateBudgetItem_eventNotFound() {
        Long nonExistingEvent = 100L;
        budgetItemRequestDTO.setOfferingCategoryId(offeringCategory.getId());
        budgetItemRequestDTO.setEventId(nonExistingEvent);

        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(eventRepository.findById(nonExistingEvent)).thenReturn(Optional.empty());

        NotFoundError exception = assertThrows(NotFoundError.class, () -> budgetItemService.createBudgetItem(budgetItemRequestDTO));
        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(nonExistingEvent);
        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());

    }

    @Test
    @DisplayName("Create budget item when budget does not exist")
    void testCreateBudgetItem_budgetNull() {
        budgetItemRequestDTO.setOfferingCategoryId(offeringCategory.getId());
        budgetItemRequestDTO.setEventId(event.getId());
        budgetItemRequestDTO.setBudget(null);

        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        InvalidRequestError error = assertThrows(InvalidRequestError.class, () -> budgetItemService.createBudgetItem(budgetItemRequestDTO));
        assertEquals(error.getMessage(),"Budget is required");

        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    @DisplayName("Create budget item when budget is less than 0")
    void testCreateBudgetItem_budgetNegative() {
        budgetItemRequestDTO.setOfferingCategoryId(offeringCategory.getId());
        budgetItemRequestDTO.setEventId(event.getId());
        budgetItemRequestDTO.setBudget(-10.0);

        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        InvalidRequestError error = assertThrows(InvalidRequestError.class, () -> budgetItemService.createBudgetItem(budgetItemRequestDTO));
        assertEquals(error.getMessage(),"Budget must be greater than 0");

        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    @DisplayName("Create budget item when new purchase created with offering category that not listed in budget items")
    void testCreatBudgetItem_newPurchase(){
        OfferingCategory offeringCategoryNew = new OfferingCategory();
        offeringCategoryNew.setId(2L);
        offeringCategoryNew.setDescription("Test new");
        offeringCategoryNew.setStatus(OfferingCategoryType.ACCEPTED);

        Offering newOffering = new Offering();
        newOffering.setId(1L);
        newOffering.setOfferingCategory(offeringCategoryNew);

        Purchase purchase = new Purchase();
        purchase.setId(1L);
        purchase.setOffering(newOffering);
        purchase.setEvent(event);
        purchase.setPrice(new Money(100, "EUR"));

        BudgetItemRequestDTO dto = BudgetItemRequestDTO.builder().build();
        dto.setBudget(purchase.getPrice().getAmount());
        dto.setEventId(purchase.getEvent().getId());
        dto.setOfferingCategoryId(purchase.getOffering().getOfferingCategory().getId());

        BudgetItem newBudgetItem = new BudgetItem();
        newBudgetItem.setId(2L);
        newBudgetItem.setMoney(new Money(100, "EUR"));
        newBudgetItem.setEvent(event);
        newBudgetItem.setOfferingCategory(offeringCategoryNew);

        budgetItemResponseDTO.setId(2L);
        budgetItemResponseDTO.setOfferingCategoryId(offeringCategoryNew.getId());

        when(offeringCategoryRepository.findById(offeringCategoryNew.getId())).thenReturn(Optional.of(offeringCategoryNew));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(budgetItemMapper.toBudgetItem(dto)).thenReturn(newBudgetItem);
        when(budgetItemRepository.save(newBudgetItem)).thenReturn(newBudgetItem);
        when(budgetItemMapper.toResponseDTO(newBudgetItem)).thenReturn(budgetItemResponseDTO);

        BudgetItemResponseDTO res = budgetItemService.createBudgetItem(dto);

        assertEquals(res.getBudget(), budgetItemResponseDTO.getBudget());
        assertEquals(res.getId(), budgetItemResponseDTO.getId());
        assertEquals(res.getEventId(), budgetItemResponseDTO.getEventId());
        assertEquals(res.getOfferingCategoryId(), budgetItemResponseDTO.getOfferingCategoryId());

        verify(offeringCategoryRepository, times(1)).findById(offeringCategoryNew.getId());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(budgetItemMapper, times(1)).toResponseDTO(newBudgetItem);
        verify(budgetItemMapper, times(1)).toBudgetItem(dto);
        verify(budgetItemRepository,times(1)).save(newBudgetItem);


    }

    @Test
    @DisplayName("Create budget item when event has already have items")
    void testCreateBudgetItem_eventHasItems(){
        OfferingCategory offeringCategoryNew = new OfferingCategory();
        offeringCategoryNew.setId(2L);
        offeringCategoryNew.setDescription("Test new");
        offeringCategoryNew.setStatus(OfferingCategoryType.ACCEPTED);

        BudgetItem newBudgetItem = new BudgetItem();
        newBudgetItem.setId(2L);
        newBudgetItem.setMoney(new Money(100, "EUR"));
        newBudgetItem.setEvent(event);
        newBudgetItem.setOfferingCategory(offeringCategoryNew);

        budgetItemRequestDTO.setOfferingCategoryId(offeringCategoryNew.getId());
        budgetItemRequestDTO.setEventId(event.getId());

        when(offeringCategoryRepository.findById(offeringCategoryNew.getId())).thenReturn(Optional.of(offeringCategoryNew));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(budgetItemMapper.toBudgetItem(budgetItemRequestDTO)).thenReturn(newBudgetItem);
        when(budgetItemRepository.save(newBudgetItem)).thenReturn(newBudgetItem);
        when(budgetItemMapper.toResponseDTO(newBudgetItem)).thenReturn(budgetItemResponseDTO);

        BudgetItemResponseDTO res = budgetItemService.createBudgetItem(budgetItemRequestDTO);

        assertEquals(res.getBudget(), budgetItemResponseDTO.getBudget());
        assertEquals(res.getId(), budgetItemResponseDTO.getId());
        assertEquals(res.getEventId(), budgetItemResponseDTO.getEventId());
        assertEquals(res.getOfferingCategoryId(), budgetItemResponseDTO.getOfferingCategoryId());

        verify(offeringCategoryRepository, times(1)).findById(offeringCategoryNew.getId());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(budgetItemMapper, times(1)).toResponseDTO(newBudgetItem);
        verify(budgetItemMapper, times(1)).toBudgetItem(budgetItemRequestDTO);
        verify(budgetItemRepository,times(1)).save(newBudgetItem);

    }

    @Test
    @DisplayName("Create budget item when offering category has bought for other event")
    void testCreateBudgetItem_otherEvent(){

        Event event1 = new Event();
        event1.setId(2L);
        event1.setDescription("Test new");
        event1.setEventType(eventType);

        BudgetItem newBudgetItem = new BudgetItem();
        newBudgetItem.setId(2L);
        newBudgetItem.setMoney(new Money(100, "EUR"));
        newBudgetItem.setEvent(event1);
        newBudgetItem.setOfferingCategory(offeringCategory);

        budgetItemRequestDTO.setEventId(event1.getId());
        budgetItemRequestDTO.setOfferingCategoryId(offeringCategory.getId());

        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(budgetItemMapper.toBudgetItem(budgetItemRequestDTO)).thenReturn(newBudgetItem);
        when(budgetItemRepository.save(newBudgetItem)).thenReturn(newBudgetItem);
        when(budgetItemMapper.toResponseDTO(newBudgetItem)).thenReturn(budgetItemResponseDTO);

        BudgetItemResponseDTO res = budgetItemService.createBudgetItem(budgetItemRequestDTO);

        assertEquals(res.getBudget(), budgetItemResponseDTO.getBudget());
        assertEquals(res.getId(), budgetItemResponseDTO.getId());
        assertEquals(res.getEventId(), budgetItemResponseDTO.getEventId());
        assertEquals(res.getOfferingCategoryId(), budgetItemResponseDTO.getOfferingCategoryId());

        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(eventRepository, times(1)).findById(event1.getId());
        verify(budgetItemMapper, times(1)).toResponseDTO(newBudgetItem);
        verify(budgetItemMapper, times(1)).toBudgetItem(budgetItemRequestDTO);
        verify(budgetItemRepository,times(1)).save(newBudgetItem);
    }

    @Test
    @DisplayName("Create budget item, item already exist")
    void testCreateBudgetItem_alreadyExist(){
        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(budgetItemMapper.toBudgetItem(budgetItemRequestDTO)).thenReturn(budgetItem);
        when(budgetItemRepository.existsByEventIdAndOfferingCategoryId(event.getId(), offeringCategory.getId()))
                .thenReturn(true);
        when(budgetItemMapper.toResponseDTO(budgetItem)).thenReturn(budgetItemResponseDTO);

        InvalidRequestError error = assertThrows(
                InvalidRequestError.class,
                () -> budgetItemService.createBudgetItem(budgetItemRequestDTO)
        );

        assertEquals("Budget item already exists for this offering category and event", error.getMessage());


        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(budgetItemMapper, times(0)).toResponseDTO(budgetItem);
        verify(budgetItemMapper, times(0)).toBudgetItem(budgetItemRequestDTO);
        verify(budgetItemRepository,times(0)).save(budgetItem);
        verify(budgetItemRepository,times(1)).existsByEventIdAndOfferingCategoryId(event.getId(), offeringCategory.getId());
    }

    @Test
    @DisplayName("Create budget item when offering category is not accepted")
    void testCreateBudgetItem_offeringCategoryNotAccepted(){
        offeringCategory.setStatus(OfferingCategoryType.PENDING);

        when(offeringCategoryRepository.findById(offeringCategory.getId())).thenReturn(Optional.of(offeringCategory));

        InvalidRequestError error = assertThrows(InvalidRequestError.class, () ->budgetItemService.createBudgetItem(budgetItemRequestDTO));
        assertEquals(error.getMessage(),"Offering category is not accepted");

        verify(offeringCategoryRepository, times(1)).findById(offeringCategory.getId());
    }

    @Test
    @DisplayName("Update budget item")
    void testUpdateBudgetItem_success() {

        budgetItemRequestDTO.setBudget(200.0);

        when(budgetItemRepository.findById(budgetItem.getId())).thenReturn(Optional.of(budgetItem));
        when(purchaseRepository.findAllByEvent(any())).thenReturn(Collections.emptyList());
        when(budgetItemRepository.save(budgetItem)).thenReturn(budgetItem);

        when(budgetItemMapper.toResponseDTO(budgetItem)).thenReturn(budgetItemResponseDTO);

        BudgetItemResponseDTO result = budgetItemService.updateBudgetItem(budgetItem.getId(), budgetItemRequestDTO);

        assertEquals(budgetItemResponseDTO, result);
        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
        verify(purchaseRepository, times(1)).findAllByEvent(any());
        verify(budgetItemRepository, times(1)).save(budgetItem);
        verify(budgetItemMapper, times(1)).toResponseDTO(budgetItem);


    }

    @Test
    @DisplayName("Update budget when budget is less than zero")
    void testUpdateBudgetItem_budgetNegative() {
        budgetItemRequestDTO.setBudget(-200.0);
        InvalidRequestError error = assertThrows(InvalidRequestError.class, () -> budgetItemService.updateBudgetItem(budgetItem.getId(), budgetItemRequestDTO));
        assertEquals(error.getMessage(), "Budget must be greater than 0");
        verify(budgetItemRepository, times(0)).findById(budgetItem.getId());
        verify(purchaseRepository, times(0)).findAllByEvent(any());
        verify(budgetItemRepository, times(0)).save(budgetItem);
        verify(budgetItemMapper, times(0)).toResponseDTO(budgetItem);
    }

    @Test
    @DisplayName("Update budget when budget does not exist")
    void testUpdateBudgetItem_notFound() {
        Long nonExistingBudgetItem = 100L;
        when(budgetItemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundError error = assertThrows(NotFoundError.class, () -> budgetItemService.updateBudgetItem(nonExistingBudgetItem, budgetItemRequestDTO));
        assertEquals(error.getMessage(), "Budget item not found");
        verify(budgetItemRepository, times(0)).findById(budgetItem.getId());
        verify(purchaseRepository, times(0)).findAllByEvent(any());
        verify(budgetItemRepository, times(0)).save(budgetItem);
        verify(budgetItemMapper, times(0)).toResponseDTO(budgetItem);
    }

    @Test
    @DisplayName("Update budget when requested budget is less than purchased offering budget")
    void testUpdateBudgetItem_budgetTooLowForPurchases() {
        budgetItemRequestDTO.setBudget(50.0);

        when(budgetItemRepository.findById(budgetItem.getId())).thenReturn(Optional.of(budgetItem));
        when(purchaseRepository.findAllByEvent(event)).thenReturn(List.of(purchase));

        InvalidRequestError error = assertThrows(InvalidRequestError.class, () -> budgetItemService.updateBudgetItem(budgetItem.getId(), budgetItemRequestDTO));
        assertEquals(error.getMessage(), "Your budget is not enough for purchased offering");
        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
        verify(purchaseRepository, times(1)).findAllByEvent(event);
        verify(budgetItemRepository, times(0)).save(budgetItem);
        verify(budgetItemMapper, times(0)).toResponseDTO(budgetItem);
    }

    @Test
    @DisplayName("Delete budget item")
    void testDeleteBudgetItem_success() {
        when(budgetItemRepository.findById(budgetItem.getId())).thenReturn(Optional.of(budgetItem));
        when(purchaseRepository.findAllByEvent(event)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = budgetItemService.deleteBudgetItem(budgetItem.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
        verify(purchaseRepository, times(1)).findAllByEvent(event);
        verify(budgetItemRepository).deleteById(budgetItem.getId());
    }

    @Test
    @DisplayName("Delete budget item when item does not exist")
    void testDeleteBudgetItem_notFound() {
        when(budgetItemRepository.findById(budgetItem.getId())).thenReturn(Optional.empty());
        NotFoundError error = assertThrows(NotFoundError.class, () -> budgetItemService.deleteBudgetItem(budgetItem.getId()));
        assertEquals(error.getMessage(), "Budget item not found");
        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
        verify(purchaseRepository, times(0)).findAllByEvent(event);
        verify(budgetItemRepository,times(0)).deleteById(budgetItem.getId());
    }

    @Test
    @DisplayName("Delete budget item when item belongs to some purchase")
    void testDeleteBudgetItem_hasPurchasedOffering() {
        when(budgetItemRepository.findById(budgetItem.getId())).thenReturn(Optional.of(budgetItem));
        when(purchaseRepository.findAllByEvent(event)).thenReturn(List.of(purchase));
        InvalidRequestError error =  assertThrows(InvalidRequestError.class, () -> budgetItemService.deleteBudgetItem(budgetItem.getId()));
        assertEquals(error.getMessage(), "Offering category belongs to purchased offering");
        verify(budgetItemRepository, times(1)).findById(budgetItem.getId());
        verify(purchaseRepository, times(1)).findAllByEvent(event);
        verify(budgetItemRepository,times(0)).deleteById(budgetItem.getId());
    }
}
