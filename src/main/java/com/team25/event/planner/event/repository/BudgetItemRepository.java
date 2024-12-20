package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {


    List<BudgetItem> findAllByEvent(Event event);
}
