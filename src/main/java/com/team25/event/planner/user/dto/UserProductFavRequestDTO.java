package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class UserProductFavRequestDTO {
    private Long userId;
    private Long productId;
}
