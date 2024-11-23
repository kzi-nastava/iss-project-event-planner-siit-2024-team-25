package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class UserOfferingFavRequestDTO {
    private Long userId;
    private Long offeringId;
}
