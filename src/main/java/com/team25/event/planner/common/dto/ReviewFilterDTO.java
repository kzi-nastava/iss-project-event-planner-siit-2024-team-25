package com.team25.event.planner.common.dto;

import com.team25.event.planner.common.model.ReviewStatus;
import lombok.Data;

@Data
public class ReviewFilterDTO {
    private ReviewStatus status;
}
