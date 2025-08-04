package com.team25.event.planner.user.mapper;

import com.team25.event.planner.user.dto.CalendarEventResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalendarEventMapper {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "eventType", constant = "EVENT")
    CalendarEventResponseDTO fromEvent(Event event);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "eventType", constant = "MY_EVENT")
    CalendarEventResponseDTO fromMyEvent(Event event);

    @Mapping(target = "title", source = "offering.name")
    @Mapping(target = "eventType", constant = "RESERVATION")
    CalendarEventResponseDTO fromPurchase(Purchase purchase);
}
