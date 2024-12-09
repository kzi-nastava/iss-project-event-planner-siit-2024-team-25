package com.team25.event.planner.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuickRegisterResponseDTO {
    private Long userId;
    private String userEmail;
    private String password;
    private Long eventId;
}
