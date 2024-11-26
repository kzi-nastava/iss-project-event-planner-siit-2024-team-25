package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class UserReportRequestDTO {
    private Long userId;
    private String report;
}
