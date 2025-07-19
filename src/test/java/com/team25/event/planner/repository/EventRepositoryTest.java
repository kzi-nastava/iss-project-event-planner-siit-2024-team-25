package com.team25.event.planner.repository;

import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:event-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private Event publicEvent;
    private Event privateEvent;

    @BeforeAll
    public void setup() {
        publicEvent = eventRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(publicEvent, "Event not found");
        privateEvent = eventRepository.findById(2L).orElse(null);
        Assertions.assertNotNull(privateEvent, "Event not found");
    }

    @Test
    @DisplayName("Can any user view public event")
    public void testCanUserViewEventPublic() {
        User user = userRepository.findById(2L).orElse(null); // not connected to event
        Assertions.assertNotNull(user);
        Assertions.assertTrue(eventRepository.canUserViewEvent(publicEvent.getId(), user.getId(), user.getAccount().getEmail()));
    }

    @Test
    @DisplayName("Can event organizer view event")
    public void testCanUserViewEventOrganizer() {
        User user = userRepository.findById(privateEvent.getOrganizer().getId()).orElse(null);
        Assertions.assertNotNull(user, "Event organizer does not exist");
        Assertions.assertTrue(eventRepository.canUserViewEvent(privateEvent.getId(), user.getId(), user.getAccount().getEmail()));
    }

    @Test
    @DisplayName("Can attendee view event")
    public void testCanUserViewEventAttendee() {
        User user = userRepository.findById(3L).orElse(null); // attendee
        Assertions.assertNotNull(user);
        Assertions.assertTrue(eventRepository.canUserViewEvent(privateEvent.getId(), user.getId(), user.getAccount().getEmail()));
    }

    @Test
    @DisplayName("Can invited user view event")
    public void testCanUserViewEventInvited() {
        User user = userRepository.findById(4L).orElse(null); // invited
        Assertions.assertNotNull(user);
        Assertions.assertTrue(eventRepository.canUserViewEvent(privateEvent.getId(), user.getId(), user.getAccount().getEmail()));
    }

    @Test
    @DisplayName("Can any user view private event")
    public void testCanUserViewEventPrivate() {
        User user = userRepository.findById(2L).orElse(null); // not connected to event
        Assertions.assertNotNull(user);
        Assertions.assertFalse(eventRepository.canUserViewEvent(privateEvent.getId(), user.getId(), user.getAccount().getEmail()));
    }
}
