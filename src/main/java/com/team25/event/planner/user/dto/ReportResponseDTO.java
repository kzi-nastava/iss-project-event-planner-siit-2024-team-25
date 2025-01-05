package com.team25.event.planner.user.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ReportResponseDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String reportMessage;
    private Long reportedUserId;
    private String reportedUserFirstName;
    private String reportedUserLastName;
    private Boolean isViewed;
    private Instant createdDate;
}
