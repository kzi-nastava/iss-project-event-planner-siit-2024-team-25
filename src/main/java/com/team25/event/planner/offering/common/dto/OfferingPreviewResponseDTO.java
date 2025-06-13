package com.team25.event.planner.offering.common.dto;

import com.team25.event.planner.offering.common.model.Offering;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfferingPreviewResponseDTO {
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

    public OfferingPreviewResponseDTO(Offering service, boolean isService) {
        this.id = service.getId();
        this.name = service.getName();
        this.description = service.getDescription();
        this.ownerName = service.getOwner().getFirstName() + ' ' + service.getOwner().getLastName();
        this.country = service.getOwner().getCompanyAddress().getCountry();
        this.city = service.getOwner().getCompanyAddress().getCity();
        this.rating = 0;
        this.price = service.getPrice();
        this.isService = isService;
    }
}
