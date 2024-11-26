package com.team25.event.planner.user.model;

import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.converter.PhoneNumberConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owners")
public class Owner extends User {
    @NotNull(message = "Company name is required")
    @Column(nullable = false)
    private String companyName;

    @Embedded
    private Location companyAddress;

    @Column(columnDefinition = "varchar(16)")
    @Convert(converter = PhoneNumberConverter.class)
    @Valid
    private PhoneNumber contactPhone;

    @NotNull(message = "Description name is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @Column(name = "company_pictures")
    private List<String> companyPictures;

    public Owner(
            Long id,
            String firstName,
            String lastName,
            String profilePictureUrl,
            UserRole userRole,
            Account account,
            List<User> blockedUsers,
            List<User> blockedByUsers,
            List<Service> favoriteServices,
            List<Product> favoriteProducts,
            String companyName,
            Location companyAddress,
            PhoneNumber contactPhone,
            String description,
            List<String> companyPictures
    ) {
        super(id, firstName, lastName, profilePictureUrl, userRole, account, blockedUsers, blockedByUsers, favoriteServices, favoriteProducts);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.contactPhone = contactPhone;
        this.description = description;
        this.companyPictures = companyPictures;
    }
}