package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.user.model.PhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Event organizer specific information
 */
@Data
public class EventOrganizerRequestDTO {
    @NotNull(message = "Living address is required")
    @Valid
    private LocationRequestDTO livingAddress;

    @NotNull(message = "Phone number is required")
    @Valid
    private PhoneNumber phoneNumber;
}
