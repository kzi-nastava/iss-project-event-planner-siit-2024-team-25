package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.service.BudgetItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/budget-items/")
public class BudgetItemController {
    private final BudgetItemService budgetItemService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    //@Secured("EVENT_ORGANIZER")
    public List<BudgetItemResponseDTO> getBudgetItems() {
        return budgetItemService.getAllBudgetItems();
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public BudgetItemResponseDTO getBudgetItemById(@PathVariable Long id) {
        return budgetItemService.getBudgetItemById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public BudgetItemResponseDTO createBudgetItem(@RequestBody BudgetItemRequestDTO budgetItemRequestDTO) {
        return budgetItemService.createBudgetItem(budgetItemRequestDTO);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public BudgetItemResponseDTO updateBudgetItem(@PathVariable Long id, @RequestBody BudgetItemRequestDTO budgetItemRequestDTO) {
        return budgetItemService.updateBudgetItem(id, budgetItemRequestDTO);
    }

    @DeleteMapping(value = "{id}")
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<?> deleteBudgetItem(@PathVariable Long id) {
        return ResponseEntity.ok(budgetItemService.deleteBudgetItem(id));
    }
}
