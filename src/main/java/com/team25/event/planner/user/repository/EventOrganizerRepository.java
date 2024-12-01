package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.EventOrganizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long>, JpaSpecificationExecutor<EventOrganizer> {
}
