package com.team25.event.planner.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ReviewStatsResponseDTO {
    private Integer reviewCount;
    private Double averageRating;
    private Map<Integer, Integer> reviewCounts;
}
