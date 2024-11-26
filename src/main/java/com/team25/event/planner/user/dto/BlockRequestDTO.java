package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class BlockRequestDTO {
    private final Long blockedUserId;
    private final String message;
}
