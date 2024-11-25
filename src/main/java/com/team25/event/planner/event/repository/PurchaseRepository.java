package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
    SELECT CASE WHEN COUNT(p) = 0 THEN true ELSE false END
    FROM Purchase p
    WHERE p.service.id = :serviceId
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
}
