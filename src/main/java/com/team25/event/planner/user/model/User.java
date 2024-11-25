package com.team25.event.planner.user.model;

import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.model.Service;
import jakarta.persistence.*;
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
// demo class
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "favorite_services"
    )
    private List<Service> favoriteServices;

    @ManyToMany
    @JoinTable(name = "favorite_products")
    private List<Product> favoriteProducts;
}
