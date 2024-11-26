package com.team25.event.planner.user.mapper;

import com.team25.event.planner.common.mapper.LocationMapper;
import com.team25.event.planner.user.dto.EventOrganizerResponseDTO;
import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.model.EventOrganizer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = LocationMapper.class)
public interface EventOrganizerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "livingAddress", source = "eventOrganizerFields.livingAddress")
    @Mapping(target = "phoneNumber", source = "eventOrganizerFields.phoneNumber")
    EventOrganizer toEventOrganizer(UserRequestDTO registerRequestDTO);

    EventOrganizerResponseDTO toDTO(EventOrganizer eventOrganizer);
}
