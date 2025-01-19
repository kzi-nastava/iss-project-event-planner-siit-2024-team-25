package com.team25.event.planner.common.dto;

import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.common.model.ReviewType;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String comment;
    private int rating;
    private ReviewType reviewType;
    private ReviewStatus reviewStatus;
    private Date createdDate;
    private Long purchaseId;
}
