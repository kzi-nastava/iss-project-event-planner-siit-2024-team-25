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
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<List<BudgetItemResponseDTO>> getBudgetItemsByEvent(@RequestParam(value = "eventId", required = false) Long eventId){
        if(eventId == null){
            return ResponseEntity.ok(budgetItemService.getAllBudgetItems());
        }
        return ResponseEntity.ok(budgetItemService.getBudgetItemsByEvent(eventId));
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<BudgetItemResponseDTO> getBudgetItemById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetItemService.getBudgetItemById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<BudgetItemResponseDTO> createBudgetItem(@RequestBody BudgetItemRequestDTO budgetItemRequestDTO) {
        return ResponseEntity.ok(budgetItemService.createBudgetItem(budgetItemRequestDTO));
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<BudgetItemResponseDTO> updateBudgetItem(@PathVariable Long id, @RequestBody BudgetItemRequestDTO budgetItemRequestDTO) {
        return ResponseEntity.ok(budgetItemService.updateBudgetItem(id, budgetItemRequestDTO));
    }

    @DeleteMapping(value = "{id}")
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<?> deleteBudgetItem(@PathVariable Long id) {
        return ResponseEntity.ok(budgetItemService.deleteBudgetItem(id));
    }
}
