package com.team25.event.planner.repository;

import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Money;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:budget-plan-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BudgetPlanRepositoryTest {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Valid find by eventId and organizerId returns budget items")
    void testFindByEventIdAndOrganizerId() {
        Event event = eventRepository.findById(1L).orElse(null);
        User organizer = userRepository.findById(1L).orElse(null);
        if(event== null || organizer == null) Assertions.fail();

        List<BudgetItem> found = budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), organizer.getId());

        Assertions.assertEquals(2, found.size());
    }
    @Test
    @DisplayName("Invalid find by eventId and organizerId when organizer did not create the event")
    void testFindByEventIdAndOrganizerIdWhenOrganizerDidNotCreateEvent() {
        Event event = eventRepository.findById(1L).orElse(null);
        User organizer = userRepository.findById(5L).orElse(null);
        if(event== null || organizer == null) Assertions.fail();
        List<BudgetItem> found = budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), organizer.getId());

        Assertions.assertEquals(0, found.size());
    }

    @Test
    @DisplayName("Invalid find by eventId and userId when user is not organizer")
    void testFindByEventIdAndUserId() {
        Event event = eventRepository.findById(1L).orElse(null);
        User organizer = userRepository.findById(2L).orElse(null);
        if(event== null || organizer == null) Assertions.fail();
        List<BudgetItem> found = budgetItemRepository.findByEventIdAndOrganizerId(event.getId(), organizer.getId());

        Assertions.assertEquals(0, found.size());
    }

    @Test
    @DisplayName("Invalid find by eventId and organizerId returns empty when no matching items")
    void testFindByEventIdAndOrganizerId_noMatch() {
        List<BudgetItem> result = budgetItemRepository.findByEventIdAndOrganizerId(999L, 888L);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Valid is suitable by offerId and eventId returns true if can be added to budget items")
    void testIsSuitableByOfferIdAndEventId() {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(2L).orElse(null);
        Event event = eventRepository.findById(2L).orElse(null);
        if(event == null || offeringCategory == null) Assertions.fail();

        boolean canBeAdded = !budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId());
        Assertions.assertTrue(canBeAdded);
    }

    @Test
    @DisplayName("Invalid is suitable by offerId and eventId returns true if can not be added to budget items")
    void testIsSuitableByOfferIdAndEventId_notSuitable() {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(1L).orElse(null);
        Event event = eventRepository.findById(1L).orElse(null);
        if(event == null || offeringCategory == null) Assertions.fail();

        boolean canBeAdded = !budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId());
        Assertions.assertFalse(canBeAdded);
    }

    @Test
    @DisplayName("Valid is suitable by offerId and eventId when budget item with eventId does not exist, with offerId exist")
    void testIsSuitableByOfferIdAndEventId_existOfferId() {
        Event event = eventRepository.findById(2L).orElse(null);
        if(event == null) Assertions.fail();
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(1L).orElse(null);


        boolean canBeAdded = !budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId());
        Assertions.assertTrue(canBeAdded);
    }

    @Test
    @DisplayName("Valid is suitable by offerId and eventId when budget item with eventId does not exist, with offerId exist")
    void testIsSuitableByOfferIdAndEventId_existEventId() {
        Event event = eventRepository.findById(1L).orElse(null);
        if(event == null) Assertions.fail();
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(2L).orElse(null);


        boolean canBeAdded = !budgetItemRepository.isSuitableByOfferIdAndNotEventId(offeringCategory.getId(), event.getId());
        Assertions.assertTrue(canBeAdded);
    }

    @Test
    @DisplayName("Valid delete all by offering category when two or more entities are deleted")
    void testDeleteAllByOfferingCategory() {

        OfferingCategory offeringCategory = offeringCategoryRepository.findById(1L).orElse(null);
        if(offeringCategory == null) Assertions.fail();
        budgetItemRepository.save(new BudgetItem(
                new Money(200, "EUR"),
                offeringCategory,
                eventRepository.findById(2L).orElse(null)
        ));
        long before = budgetItemRepository.count();

        budgetItemRepository.deleteAllByOfferingCategory(offeringCategory.getId());
        Assertions.assertEquals(before -2 , budgetItemRepository.count());

    }

    @Test
    @DisplayName("Valid delete one by offering category when one entity is deleted")
    void testDeleteOneByOfferingCategory() {
        long before = budgetItemRepository.count();

        OfferingCategory offeringCategory = offeringCategoryRepository.findById(1L).orElse(null);
        if(offeringCategory == null) Assertions.fail();
        budgetItemRepository.deleteAllByOfferingCategory(offeringCategory.getId());
        Assertions.assertEquals(before -1 , budgetItemRepository.count());

    }

    @Test
    @DisplayName("Invalid delete all by offering category when category does not exist")
    void testDeleteAllByOfferingCategory_nonExisting() {
        long before = budgetItemRepository.count();
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(2L).orElse(null);
        if(offeringCategory == null) Assertions.fail();
        budgetItemRepository.deleteAllByOfferingCategory(offeringCategory.getId());
        Assertions.assertEquals(before, budgetItemRepository.count());
    }


}
