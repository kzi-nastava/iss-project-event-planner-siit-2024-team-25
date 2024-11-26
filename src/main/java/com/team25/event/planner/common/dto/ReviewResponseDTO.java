package com.team25.event.planner.common.dto;

import com.team25.event.planner.common.model.ReviewStatus;
import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String comment;
    private int rating;
    private ReviewStatus status;
}
