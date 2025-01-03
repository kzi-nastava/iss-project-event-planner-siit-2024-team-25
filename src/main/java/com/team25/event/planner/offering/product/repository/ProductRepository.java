package com.team25.event.planner.offering.product.repository;

import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.user.model.Owner;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findAllByOwner(@NotNull(message = "Owner is required") Owner owner);
}
