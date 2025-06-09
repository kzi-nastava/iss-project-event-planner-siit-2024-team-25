package com.team25.event.planner.event.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.mapper.BudgetItemMapper;
import com.team25.event.planner.event.model.*;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BudgetItemService {
    private final BudgetItemRepository budgetItemRepository;
    private final EventRepository eventRepository;
    private final BudgetItemMapper budgetItemMapper;
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final PurchaseRepository purchaseRepository;

    public List<BudgetItemResponseDTO> getAllBudgetItems() {
        return budgetItemRepository.findAll().stream().map(budgetItemMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<BudgetItemResponseDTO> getBudgetItemsByEvent(Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(NotFoundError::new);
        return budgetItemRepository.findAllByEvent(event).stream().map(budgetItemMapper::toResponseDTO).collect(Collectors.toList());
    }
    public boolean isSuitableByOfferIdAndNotEventId(Long OCId, Long eventId){
        eventRepository.findById(eventId).orElseThrow(NotFoundError::new);
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(OCId).orElseThrow(NotFoundError::new);
        BudgetItem budgetItem = budgetItemRepository.findByOfferingCategory(offeringCategory);
        if(budgetItem==null){
            return true;
        }
        return budgetItemRepository.isSuitableByOfferIdAndNotEventId(OCId, eventId);

    }

    public BudgetItemResponseDTO getBudgetItemById(Long id) {
        BudgetItem budgetItem = budgetItemRepository.findById(id).orElseThrow(()-> new NotFoundError("Budget item not found"));
        return budgetItemMapper.toResponseDTO(budgetItem);
    }

    public BudgetItemResponseDTO createBudgetItem(BudgetItemRequestDTO budgetItemRequestDTO) {
        BudgetItem budgetItem = budgetItemMapper.toBudgetItem(budgetItemRequestDTO);
        budgetItem.setMoney(new Money(budgetItemRequestDTO.getBudget()));
        return budgetItemMapper.toResponseDTO(budgetItemRepository.save(budgetItem));
    }

    public BudgetItemResponseDTO updateBudgetItem(Long id, BudgetItemRequestDTO budgetItemRequestDTO) {
        BudgetItem budgetItem = budgetItemRepository.findById(id).orElseThrow(()-> new NotFoundError("Budget item not found"));

        List<Purchase> purchases = purchaseRepository.findAllByEvent(budgetItem.getEvent());
        double suma = 0.0;
        for(Purchase purchase : purchases){
            if(purchase.getOffering().getOfferingCategory() == budgetItem.getOfferingCategory()){
                suma += purchase.getOffering().getPrice();
                System.out.println(suma);
                if(suma > budgetItemRequestDTO.getBudget()){
                    throw new InvalidRequestError("Your budget is not enough for purchased offering");
                }
            }
        }
        budgetItem.setMoney(new Money(budgetItemRequestDTO.getBudget(), "EUR"));
        return budgetItemMapper.toResponseDTO(budgetItemRepository.save(budgetItem));
    }
    public ResponseEntity<?> deleteBudgetItem(Long id) {
        BudgetItem budgetItem = budgetItemRepository.findById(id).orElseThrow(()-> new NotFoundError("Budget item not found"));
        List<Purchase> purchases = purchaseRepository.findAllByEvent(budgetItem.getEvent());
        for(Purchase purchase : purchases){
            if(purchase.getOffering().getOfferingCategory() == budgetItem.getOfferingCategory()){
                throw new InvalidRequestError("Offering category belongs to purchased offering");
            }
        }

        budgetItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
