package com.team25.event.planner.offering.product.model;

import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Offering {
    public Product(Long id, String name, String description, double price, double discount,
                   List<String> images, boolean isVisible, boolean isAvailable, OfferingType status, Collection<EventType> eventTypes,
                   OfferingCategory offeringCategory, Owner owner, List<User> users) {
        super(id, name, description, price, discount, images, isVisible, isAvailable, status, eventTypes, offeringCategory, owner);
        this.users = users;
    }

    @ManyToMany(mappedBy = "favoriteProducts")
    private List<User> users;
}
