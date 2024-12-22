package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
public class BudgetItemMapperHelper {
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final EventRepository eventRepository;

    public BudgetItemMapperHelper(OfferingCategoryRepository offeringCategoryRepository, EventRepository eventRepository) {
        this.offeringCategoryRepository = offeringCategoryRepository;
        this.eventRepository = eventRepository;
    }

    @AfterMapping
    public void mapToBudgetItem(
            @MappingTarget BudgetItem budgetItem,
            BudgetItemRequestDTO budgetItemRequestDTO
    ){
        budgetItem.setOfferingCategory(getOfferingCategory(budgetItemRequestDTO.getOfferingCategoryId()));
        budgetItem.setEvent(getEvent(budgetItemRequestDTO.getEventId()));
    }

    private OfferingCategory getOfferingCategory(Long id){
        return offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No OfferingCategory found"));
    }

    private Event getEvent(Long id){
        return id != null ? eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No Event found")) : null;
    }
}
