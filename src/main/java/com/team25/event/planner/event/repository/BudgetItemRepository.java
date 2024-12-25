package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {


    List<BudgetItem> findAllByEvent(Event event);

    @Query("SELECT COUNT(b) > 0 FROM BudgetItem b WHERE b.offeringCategory.id = :offerId AND b.event.id != :eventId")
    boolean isSuitableByOfferIdAndNotEventId(@Param("offerId") Long offerId, @Param("eventId") Long eventId);

    BudgetItem findByOfferingCategory(OfferingCategory offeringCategory);

    Optional<BudgetItem> findBudgetItemByEventIdAndOfferingCategoryId(Long eventId, Long categoryId);
}
