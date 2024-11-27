package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.EventInvitationRequestDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventInvitation;
import com.team25.event.planner.event.model.EventInvitationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventInvitationMapper {

    @Mapping(target = "status", source = "status")
    EventInvitation toEventInvitation(EventInvitationRequestDTO requestDTO, Event event, EventInvitationStatus status);
}
