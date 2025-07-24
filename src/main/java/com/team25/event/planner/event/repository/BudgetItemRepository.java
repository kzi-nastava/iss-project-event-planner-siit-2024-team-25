package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.dto.BudgetItemResponseDTO;
import com.team25.event.planner.event.model.BudgetItem;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {


    @Query("SELECT b FROM BudgetItem b WHERE b.event.id = :eventId AND b.event.organizer.id = :organizerId")
    List<BudgetItem> findByEventIdAndOrganizerId(@Param("eventId") Long eventId, @Param("organizerId") Long organizerId);



    @Query("SELECT COUNT(b) > 0 FROM BudgetItem b WHERE b.offeringCategory.id = :offerId AND b.event.id = :eventId")
    boolean isSuitableByOfferIdAndNotEventId(@Param("offerId") Long offerId, @Param("eventId") Long eventId);

    Optional<BudgetItem> findBudgetItemByEventIdAndOfferingCategoryId(Long eventId, Long categoryId);
    boolean existsByEventIdAndOfferingCategoryId(Long eventId, Long offeringCategoryId);

    @Modifying
    @Query("DELETE FROM BudgetItem b WHERE b.offeringCategory.id = :offeringId")
    void deleteAllByOfferingCategory(Long offeringId);
}
