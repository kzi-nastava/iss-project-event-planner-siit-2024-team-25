package com.team25.event.planner.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinEventRequestDTO {
    @NotNull
    private final Long userId;
}
