package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapperHelper {
    private final CurrentUserService currentUserService;

    @AfterMapping
    public void addFavoriteFlag(@MappingTarget EventResponseDTO dto, Event event) {
        User currentUser = currentUserService.getCurrentUser();
        dto.setIsFavorite(currentUser != null && currentUser.getFavoriteEvents().contains(event));
    }

    @AfterMapping
    public void addFavoriteFlag(@MappingTarget EventPreviewResponseDTO dto, Event event) {
        User currentUser = currentUserService.getCurrentUser();
        dto.setIsFavorite(currentUser != null && currentUser.getFavoriteEvents().contains(event));
    }
}
