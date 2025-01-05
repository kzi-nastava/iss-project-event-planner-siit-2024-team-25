package com.team25.event.planner.offering.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private Double discount;

    private List<MultipartFile> images;

    private boolean isVisible;

    private boolean isAvailable;

    private List<Long> eventTypeIds;

    private Long offeringCategoryId;

    private String offeringCategoryName;

    @NotNull(message = "Owner is required")
    private Long ownerId;

    private List<String> imagesToDelete;
}