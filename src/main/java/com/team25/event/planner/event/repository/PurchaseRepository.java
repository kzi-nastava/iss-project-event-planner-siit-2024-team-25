package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {

    @Query("""
    SELECT CASE WHEN COUNT(p) = 0 THEN false ELSE true END
    FROM Purchase p
    WHERE p.offering.id = :serviceId
      AND(
          (p.startDate < :endDate AND p.endDate > :startDate)
          OR (p.startDate = :endDate AND p.startTime < :endTime)
          OR (p.endDate = :startDate AND p.endTime > :startTime)
      )
    """)
    boolean isAvailable(@Param("serviceId") Long serviceId,
                        @Param("startDate") LocalDate startDate,
                        @Param("startTime") LocalTime startTime,
                        @Param("endDate") LocalDate endDate,
                        @Param("endTime") LocalTime endTime);

    @Query("""
    SELECT p
    FROM Purchase p
    WHERE p.offering.id = :serviceId
      AND(
          (p.startDate < :endDate AND p.endDate > :startDate)
          OR (p.startDate = :endDate AND p.startTime < :endTime)
          OR (p.endDate = :startDate AND p.endTime > :startTime)
      )
    """)
    List<Purchase> getPurchase(@Param("serviceId") Long serviceId,
                               @Param("startDate") LocalDate startDate,
                               @Param("startTime") LocalTime startTime,
                               @Param("endDate") LocalDate endDate,
                               @Param("endTime") LocalTime endTime);

    List<Purchase> findPurchaseByEventIdAndOfferingOfferingCategory(Long id, OfferingCategory offeringCategory);
}
