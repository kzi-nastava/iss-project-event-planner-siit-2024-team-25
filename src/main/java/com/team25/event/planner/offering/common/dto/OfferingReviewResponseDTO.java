package com.team25.event.planner.offering.common.dto;

import lombok.Data;

@Data
public class OfferingReviewResponseDTO {
    private final Long id;
    private final Long offeringId;
    private final int rate;
    private final String comment;
}
