package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
