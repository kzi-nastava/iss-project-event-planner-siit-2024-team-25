package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {
}
