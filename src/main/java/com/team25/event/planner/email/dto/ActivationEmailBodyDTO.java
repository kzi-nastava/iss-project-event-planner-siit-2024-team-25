package com.team25.event.planner.email.dto;

import lombok.Data;

@Data
public class ActivationEmailBodyDTO {
    private final String name;
    private final String activationUrl;
}
