package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.EventInvitationEmailDTO;
import com.team25.event.planner.event.dto.EventInvitationRequestDTO;
import com.team25.event.planner.event.dto.EventInvitationShortEmailDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventInvitation;
import com.team25.event.planner.event.model.EventInvitationStatus;
import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventInvitationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "event", source = "event")
    EventInvitation toEventInvitation(EventInvitationRequestDTO requestDTO, Event event, EventInvitationStatus status);

    @Mapping(target = "guestFirstName", source = "user.firstName")
    @Mapping(target = "guestLastName", source = "user.lastName")
    @Mapping(target = "eventName", source = "event.name")
    @Mapping(target = "eventDescription", source = "event.description")
    @Mapping(target = "eventDate", source = "event.startDate")
    @Mapping(target = "eventTime", source = "event.startTime")
    @Mapping(target = "eventCountry", source = "event.location.country")
    @Mapping(target = "eventCity", source = "event.location.city")
    @Mapping(target = "eventAddress", source = "event.location.address")
    @Mapping(target = "eventInvitationCode", source = "eventInvitation")
    EventInvitationEmailDTO toEventInvitationEmailDTO(User user, Event event, String eventInvitation);


    @Mapping(target = "eventName", source = "event.name")
    @Mapping(target = "eventDescription", source = "event.description")
    @Mapping(target = "eventDate", source = "event.startDate")
    @Mapping(target = "eventTime", source = "event.startTime")
    @Mapping(target = "eventCountry", source = "event.location.country")
    @Mapping(target = "eventCity", source = "event.location.city")
    @Mapping(target = "eventAddress", source = "event.location.address")
    @Mapping(target = "eventInvitationCode", source = "eventInvitation")
    EventInvitationShortEmailDTO toEventInvitationShortEmailDto( Event event, String eventInvitation);
}
