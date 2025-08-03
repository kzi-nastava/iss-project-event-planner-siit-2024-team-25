package com.team25.event.planner.offering.product.dto;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductPreviewResponseDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String description;
    private String country;
    private String city;
    private double rating;
    private double price;
    private Boolean isService;
    private Boolean isFavorite;
    private String thumbnail;

    public ProductPreviewResponseDTO(OfferingPreviewResponseDTO offering, String thumbnail) {
        id = offering.getId();
        name = offering.getName();
        ownerName = offering.getOwnerName();
        description = offering.getDescription();
        country = offering.getCountry();
        city = offering.getCity();
        rating = offering.getRating();
        price = offering.getPrice();
        isService = false;
        isFavorite = offering.getIsFavorite();
        this.thumbnail = thumbnail;
    }
}
