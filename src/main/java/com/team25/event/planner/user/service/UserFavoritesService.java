package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.mapper.EventMapper;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.user.dto.FavoriteEventRequestDTO;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFavoritesService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public List<EventPreviewResponseDTO> getFavoriteEvents(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        return user.getFavoriteEvents().stream().map(eventMapper::toEventPreviewResponseDTO).toList();
    }

    public EventPreviewResponseDTO addFavoriteEvent(Long userId, FavoriteEventRequestDTO requestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

        Event event = eventRepository.findById(requestDTO.getEventId())
                .orElseThrow(() -> new NotFoundError("Event not found"));

        user.getFavoriteEvents().add(event);
        userRepository.save(user);

        return eventMapper.toEventPreviewResponseDTO(event);
    }

    public void removeEventFromFavorites(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        user.getFavoriteEvents().removeIf(event -> event.getId().equals(eventId));
        userRepository.save(user);
    }
}
