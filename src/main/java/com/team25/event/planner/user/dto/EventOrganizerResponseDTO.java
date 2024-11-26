package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.user.model.PhoneNumber;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventOrganizerResponseDTO extends UserResponseDTO {
    private LocationResponseDTO livingAddress;
    private PhoneNumber phoneNumber;
}
