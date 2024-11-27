package com.team25.event.planner.common.dto;

import com.team25.event.planner.common.model.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    @NotNull
    private Long reviewId;
    @NotNull
    private ReviewStatus status;
}
