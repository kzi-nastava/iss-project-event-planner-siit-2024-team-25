package com.team25.event.planner.offering.product.dto;

import com.team25.event.planner.offering.common.model.OfferingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    private Double price;

    private Double discount;

    private List<String> images;

    private boolean isVisible;

    private boolean isAvailable;

    private OfferingType status;

    private List<Long> eventTypeIds;

    @NotNull(message = "Offering category is required")
    private Long offeringCategoryId;

    @NotNull(message = "Owner is required")
    private Long ownerId;
}