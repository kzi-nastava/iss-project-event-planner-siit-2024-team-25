package com.team25.event.planner.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationRequestDTO {
    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address is required")
    private String address;
}
