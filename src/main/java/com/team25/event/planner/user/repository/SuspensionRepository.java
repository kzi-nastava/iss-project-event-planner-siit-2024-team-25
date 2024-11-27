package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.Suspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SuspensionRepository  extends JpaRepository<Suspension, Long>, JpaSpecificationExecutor<Suspension> {
}
