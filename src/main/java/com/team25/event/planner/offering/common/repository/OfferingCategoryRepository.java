package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.model.OfferingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferingCategoryRepository extends JpaRepository<OfferingCategory, Long> {
}
