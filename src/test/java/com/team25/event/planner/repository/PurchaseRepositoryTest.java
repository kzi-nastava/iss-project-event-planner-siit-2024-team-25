package com.team25.event.planner.repository;

import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:purchase-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PurchaseRepositoryTest {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @BeforeEach
    public void setup(){
        Event event = eventRepository.findById(1L).orElse(null);
        event.setStartDate(LocalDate.now().plusYears(1));
        event.setEndDate(LocalDate.now().plusYears(1));
        eventRepository.save(event);
    }

    @Test
    @DisplayName("Valid find total spent by event id and offering category id when there is only one valid purchase")
    //service2 and service3 has same offering category
    public void testFindTotalSpentByEventIdAndOfferingCategoryIdWithOneValidPurchase(){
        Event event = eventRepository.findById(1L).orElse(null);

        Service service = serviceRepository.findById(1L).orElse(null);
        Service service2 = serviceRepository.findById(2L).orElse(null);
        Service service3 = serviceRepository.findById(3L).orElse(null);

        Purchase purchase1 = new Purchase(null, new Money(service.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service, null);
        Purchase purchase2 = new Purchase(null, new Money(service2.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service2, null);
        Purchase purchase3 = new Purchase(null, new Money(service3.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service3, null);

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        double expectedSpent = service.getPrice();

        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event.getId(), service.getOfferingCategory().getId());
        Assertions.assertEquals(expectedSpent, totalSpent);
    }

    @Test
    @DisplayName("Valid find total spent by event id and offering category id when there are more valid purchases")
    //service2 and service3 has same offering category
    public void testFindTotalSpentByEventIdAndOfferingCategoryIdWithMoreValidPurchase(){
        Event event = eventRepository.findById(1L).orElse(null);

        Service service = serviceRepository.findById(1L).orElse(null);
        Service service2 = serviceRepository.findById(2L).orElse(null);
        Service service3 = serviceRepository.findById(3L).orElse(null);

        Purchase purchase1 = new Purchase(null, new Money(service.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service, null);
        Purchase purchase2 = new Purchase(null, new Money(service2.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service2, null);
        Purchase purchase3 = new Purchase(null, new Money(service3.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service3, null);

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        double expectedSpent = service2.getPrice() + service3.getPrice();

        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event.getId(), service2.getOfferingCategory().getId());
        Assertions.assertEquals(expectedSpent, totalSpent);
    }

    @Test
    @DisplayName("Valid find total spent by event id and offering category id when there is no valid purchase")
    //service2 and service3 has same offering category
    public void testFindTotalSpentByEventIdAndOfferingCategoryIdWithNoValidPurchase(){
        Event event = eventRepository.findById(1L).orElse(null);

        Service service = serviceRepository.findById(1L).orElse(null);

        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event.getId(), service.getOfferingCategory().getId());
        Assertions.assertEquals(0, totalSpent);
    }

    @Test
    @DisplayName("Invalid find total spent by event id and offering category id when there is purchases for event and not for offering category")
    public void testFindTotalSpentByEventIdAndOfferingCategoryIdInvalidOfferingCategoryId(){
        Event event = eventRepository.findById(1L).orElse(null);

        Service service = serviceRepository.findById(1L).orElse(null);
        Service service2 = serviceRepository.findById(2L).orElse(null);

        Purchase purchase1 = new Purchase(null, new Money(service.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service, null);
        Purchase purchase2 = new Purchase(null, new Money(service2.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service, null);

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);

        double expectedSpent = 0;

        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event.getId(), service2.getOfferingCategory().getId());
        Assertions.assertEquals(expectedSpent, totalSpent);
    }


    @Test
    @DisplayName("Invalid find total spent by event id and offering category id when there is purchases for offering category and not for event")
    public void testFindTotalSpentByEventIdAndOfferingCategoryIdInvalidEventId(){
        Event event = eventRepository.findById(1L).orElse(null);
        Event event2 = eventRepository.findById(2L).orElse(null);

        Service service = serviceRepository.findById(1L).orElse(null);
        Service service2 = serviceRepository.findById(2L).orElse(null);

        Purchase purchase1 = new Purchase(null, new Money(service.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service, null);
        Purchase purchase2 = new Purchase(null, new Money(service2.getPrice()), event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getStartTime(), event, service, null);

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);

        double expectedSpent = 0;

        double totalSpent = purchaseRepository.findTotalSpentByEventIdAndOfferingCategoryId(event2.getId(), service.getOfferingCategory().getId());
        Assertions.assertEquals(expectedSpent, totalSpent);
    }

}
