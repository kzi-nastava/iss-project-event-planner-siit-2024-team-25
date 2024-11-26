package com.team25.event.planner.common.dto;

import lombok.Data;

@Data
public class LocationResponseDTO {
    private final String country;
    private final String city;
    private final String address;
    private final Long latitude;
    private final Long longitude;
}
