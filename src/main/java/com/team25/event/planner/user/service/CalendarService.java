package com.team25.event.planner.user.service;

import com.team25.event.planner.user.dto.CalendarEventResponseDTO;
import com.team25.event.planner.user.mapper.CalendarEventMapper;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.service.EventService;
import com.team25.event.planner.event.service.PurchaseService;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final UserRepository userRepository;
    private final EventService eventService;
    private final PurchaseService purchaseService;
    private final CalendarEventMapper calendarEventMapper;

    public List<CalendarEventResponseDTO> getCalendarEvents(Long userId, LocalDate startDate, LocalDate endDate) {
        final List<CalendarEventResponseDTO> attendingEvents = eventService
                .findAttendingEventsOverlappingDateRange(userId, startDate, endDate)
                .stream().map(calendarEventMapper::fromEvent).toList();

        final List<CalendarEventResponseDTO> events = new ArrayList<>(attendingEvents);

        final User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

        if (user instanceof EventOrganizer) {
            final List<CalendarEventResponseDTO> organizerEvents = eventService
                    .findOrganizerEventsOverlappingDateRange(userId, startDate, endDate)
                    .stream().map(calendarEventMapper::fromMyEvent).toList();
            events.addAll(organizerEvents);
        }

        if (user instanceof Owner) {
            final List<CalendarEventResponseDTO> reservations = purchaseService
                    .findOwnerPurchasesOverlappingDateRange(userId, startDate, endDate)
                    .stream().map(calendarEventMapper::fromPurchase).toList();
            events.addAll(reservations);
        }

        return events;
    }
}
