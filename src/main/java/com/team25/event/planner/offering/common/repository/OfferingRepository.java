package com.team25.event.planner.offering.common.repository;

import com.team25.event.planner.offering.common.model.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfferingRepository extends JpaRepository<Offering, Long>, JpaSpecificationExecutor<Offering> {
}
