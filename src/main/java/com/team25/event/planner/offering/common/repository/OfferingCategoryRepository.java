package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.model.OfferingCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferingCategoryRepository extends JpaRepository<OfferingCategory, Long> {
    @Query(value = "SELECT CASE WHEN EXISTS ( " +
            "SELECT 1 FROM public.offering_type ot " +
            "LEFT OUTER JOIN public.offerings o " +
            "ON ot.id = o.offering_category_id " +
            "WHERE ot.id = :id AND o.id IS NULL) " +
            "THEN TRUE ELSE FALSE END",
            nativeQuery = true)
    boolean existsInUnlinkedOfferingType(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.offering_type yy WHERE yy.id = :id", nativeQuery = true)
    int deleteOfferingTypeById(@Param("id") Long id);
}
