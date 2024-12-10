package com.team25.event.planner.offering.product.repository;

import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
}
