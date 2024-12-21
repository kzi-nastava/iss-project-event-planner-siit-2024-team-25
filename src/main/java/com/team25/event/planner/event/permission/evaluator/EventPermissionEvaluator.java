package com.team25.event.planner.event.permission.evaluator;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.PrivacyType;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPermissionEvaluator {
    private final EventRepository eventRepository;

    public boolean canView(Authentication authentication, Long eventId) {
        final UserDetailsImpl user = ((UserDetailsImpl) authentication.getPrincipal());

        if (user == null) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
            return event.getPrivacyType().equals(PrivacyType.PUBLIC);
        }

        return eventRepository.canUserViewEvent(eventId, user.getUserId(), user.getUsername());
    }
}
