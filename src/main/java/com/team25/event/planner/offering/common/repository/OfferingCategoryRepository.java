package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OfferingCategoryRepository extends JpaRepository<OfferingCategory, Long> {

    @Modifying
    @Query(value = "DELETE FROM event_types_offering_categories WHERE offering_categories_id = :offeringId", nativeQuery = true)
    void deleteCategoryFromEventTypes(Long offeringId);

    @Query(value = "SELECT CASE WHEN EXISTS ( " +
            "SELECT 1 FROM public.offering_type ot " +
            "LEFT OUTER JOIN public.offerings o " +
            "ON ot.id = o.offering_category_id " +
            "WHERE ot.id = :id AND o.id IS NULL AND o.deleted is not false) " +
            "THEN TRUE ELSE FALSE END",
            nativeQuery = true)
    boolean existsInUnlinkedOfferingType(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.offering_type yy WHERE yy.id = :id", nativeQuery = true)
    int deleteOfferingTypeById(@Param("id") Long id);

    @Query(value = "select * from offering_type o\n" +
            "where o.id = :id and o.status = :status", nativeQuery = true)
    OfferingCategory findOfferingCategoryByIdAndStatus(@Param("id") Long id, @Param("status") String status);

    List<OfferingCategory> findOfferingCategoriesByStatus(OfferingCategoryType offeringCategoryType);
}
