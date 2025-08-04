package com.team25.event.planner.offering.common.dto;

import com.team25.event.planner.common.model.ReviewStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OfferingReviewResponseDTO {
    private final Long id;
    private final Long offeringId;
    private final int rate;
    private final String comment;
    private final ReviewStatus status;
}
