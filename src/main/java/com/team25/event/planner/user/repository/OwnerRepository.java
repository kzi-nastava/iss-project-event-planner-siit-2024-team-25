package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}