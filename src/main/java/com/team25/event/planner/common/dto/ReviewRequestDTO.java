package com.team25.event.planner.common.dto;

import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.common.model.ReviewType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    private String comment;
    private int rating;
    private ReviewType reviewType;
    private Long purchaseId;
    private Long userId;
}
