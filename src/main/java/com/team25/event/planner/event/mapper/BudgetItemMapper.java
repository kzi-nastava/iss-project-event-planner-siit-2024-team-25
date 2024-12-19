package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.BudgetItemRequestDTO;
import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.model.BudgetItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BudgetItemMapperHelper.class})
public interface BudgetItemMapper {
    @Mapping(source = "offeringCategory.id", target = "offeringCategoryId")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "money.amount", target = "budget")
    @Mapping(source = "id", target = "id")
    BudgetItemResponseDTO toResponseDTO(BudgetItem entity);


    BudgetItem toBudgetItem(BudgetItemRequestDTO dto);
}
