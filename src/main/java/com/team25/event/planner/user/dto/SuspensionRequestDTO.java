package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class SuspensionRequestDTO {
    private Long userId;
    private Long reportId;
}
