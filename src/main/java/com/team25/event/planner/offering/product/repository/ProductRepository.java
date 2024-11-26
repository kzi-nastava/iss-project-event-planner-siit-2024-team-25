package com.team25.event.planner.offering.product.repository;

import com.team25.event.planner.offering.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
