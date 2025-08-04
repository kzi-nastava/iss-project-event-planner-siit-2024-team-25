package com.team25.event.planner.communication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationFilterDTO {

    private Boolean isViewed;

}
