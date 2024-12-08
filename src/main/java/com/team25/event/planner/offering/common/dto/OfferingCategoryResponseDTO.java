package com.team25.event.planner.offering.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferingCategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
}
