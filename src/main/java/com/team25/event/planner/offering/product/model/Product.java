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


    @ManyToMany(mappedBy = "favoriteProducts")
    private List<User> users;
}
