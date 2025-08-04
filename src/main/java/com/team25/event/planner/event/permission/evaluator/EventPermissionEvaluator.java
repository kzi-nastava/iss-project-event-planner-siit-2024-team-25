package com.team25.event.planner.event.permission.evaluator;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.PrivacyType;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.security.user.UserDetailsImpl;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPermissionEvaluator {
    private final EventRepository eventRepository;

    public boolean canView(Authentication authentication, Long eventId) {
        boolean isAuthenticated = true;
        if (authentication == null || authentication.getPrincipal() == null) {
            isAuthenticated = false;
        } else if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            isAuthenticated = false;
        }

        if (!isAuthenticated) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
            return event.getPrivacyType().equals(PrivacyType.PUBLIC);
        }

        final UserDetailsImpl user = ((UserDetailsImpl) authentication.getPrincipal());

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        EventOrganizer eventOrganizer = event.getOrganizer();
        if (eventOrganizer.getBlockedUsers().stream().map(User::getId).toList().contains(user.getUserId())) {
            return false;
        }

        return eventRepository.canUserViewEvent(eventId, user.getUserId(), user.getUsername());
    }

    public boolean canEdit(Authentication authentication, Long eventId) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        } else if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return false;
        }
        final UserDetailsImpl user = ((UserDetailsImpl) authentication.getPrincipal());
        if (user == null) return false;

        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundError("Event not found"));
        return event.getOrganizer().getId().equals(user.getUserId());
    }
}
