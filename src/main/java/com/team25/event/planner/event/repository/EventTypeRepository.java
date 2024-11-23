package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
}