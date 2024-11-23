package com.team25.event.planner.user.dto;

import lombok.Data;

// The class created because we are not using services and repo
@Data
public class UserProductFavResponseDTO {
    private Long id;
    private Long userId;
    private Long productId;
}
