package com.team25.event.planner.offering.common.dto;

import lombok.Data;

@Data
public class OfferingReviewRequestDTO {
    private final Long offeringId;
    private final int rate; // 1-5?
    private final String comment;
}
