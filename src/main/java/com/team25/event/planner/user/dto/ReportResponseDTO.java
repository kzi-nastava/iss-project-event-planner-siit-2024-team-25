package com.team25.event.planner.user.dto;

import lombok.Data;

@Data
public class ReportResponseDTO {
    private Long userId;
    private String reportMessage;
    private Long reportedUserId;
    private String reportedUserFirstName;
    private String reportedUserLastName;
    private boolean isViewed;

}
