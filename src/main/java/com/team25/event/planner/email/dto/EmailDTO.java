package com.team25.event.planner.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDTO {
    private String recipientEmail;
    private String subject;
    private String body;
}
