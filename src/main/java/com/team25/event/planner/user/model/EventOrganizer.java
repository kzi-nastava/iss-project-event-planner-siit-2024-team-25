package com.team25.event.planner.user.model;

import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.converter.PhoneNumberConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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
@Table(name = "event_organizers")
public class EventOrganizer extends User {
    @Embedded
    private Location livingAddress;

    @Column(columnDefinition = "varchar(16)")
    @Convert(converter = PhoneNumberConverter.class)
    @Valid
    private PhoneNumber phoneNumber;

    public EventOrganizer(Long id, String firstName, String lastName, String profilePictureUrl, UserRole userRole,
                          Account account, List<User> blockedUsers, List<User> blockedByUsers,
                          List<Service> favoriteServices, List<Product> favoriteProducts,
                          Location livingAddress, PhoneNumber phoneNumber) {
        super(id, firstName, lastName, profilePictureUrl, userRole, account, blockedUsers, blockedByUsers, favoriteServices, favoriteProducts, null,null);
        this.livingAddress = livingAddress;
        this.phoneNumber = phoneNumber;
    }
}
