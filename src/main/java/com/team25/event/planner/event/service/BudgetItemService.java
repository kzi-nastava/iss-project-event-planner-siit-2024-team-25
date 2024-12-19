package com.team25.event.planner.event.service;

import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BudgetItemService {
    private final BudgetItemRepository budgetItemRepository;


}
