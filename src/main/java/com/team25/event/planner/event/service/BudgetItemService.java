package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.mapper.BudgetItemMapper;
import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Money;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BudgetItemService {
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetItemMapper budgetItemMapper;

    public List<BudgetItemResponseDTO> getAllBudgetItems() {
        return budgetItemRepository.findAll().stream().map(budgetItemMapper::toResponseDTO).collect(Collectors.toList());
    }

    public BudgetItemResponseDTO getBudgetItemById(Long id) {
        BudgetItem budgetItem = budgetItemRepository.findById(id).orElseThrow(()-> new NotFoundError("Budget item not found"));
        return budgetItemMapper.toResponseDTO(budgetItem);
    }

    public BudgetItemResponseDTO createBudgetItem(BudgetItemRequestDTO budgetItemRequestDTO) {
        BudgetItem budgetItem = budgetItemMapper.toBudgetItem(budgetItemRequestDTO);
        budgetItem.setMoney(new Money(budgetItemRequestDTO.getBudget(), "RSD"));
        return budgetItemMapper.toResponseDTO(budgetItemRepository.save(budgetItem));
    }

    public BudgetItemResponseDTO updateBudgetItem(Long id, BudgetItemRequestDTO budgetItemRequestDTO) {
        BudgetItem budgetItem = budgetItemRepository.findById(id).orElseThrow(()-> new NotFoundError("Budget item not found"));
        budgetItem.setMoney(new Money(budgetItemRequestDTO.getBudget(), "RSD"));
        return budgetItemMapper.toResponseDTO(budgetItemRepository.save(budgetItem));
    }
    public ResponseEntity<?> deleteBudgetItem(Long id) {
        if(!budgetItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        budgetItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
