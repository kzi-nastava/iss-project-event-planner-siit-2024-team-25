package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {
    @Query("SELECT COALESCE(SUM(p.price.amount),0) " +
            "FROM Purchase p " +
            "WHERE p.event.id = :eventId AND p.offering.offeringCategory.id = :categoryId")
    Double findTotalSpentByEventIdAndOfferingCategoryId(@Param("eventId") Long eventId,
                                                        @Param("categoryId") Long categoryId);

    boolean existsByEventOrganizerIdAndStartDateGreaterThanEqual(Long eventOrganizerId, LocalDate startDateAfter);

    boolean existsByOfferingOwnerIdAndStartDateGreaterThanEqual(Long offeringOwnerId, LocalDate startDateAfter);

    List<Purchase> findByOfferingOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long ownerId, LocalDate startDate, LocalDate endDate);
}
