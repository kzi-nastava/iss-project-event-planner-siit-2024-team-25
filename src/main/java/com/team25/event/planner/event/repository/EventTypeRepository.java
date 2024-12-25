package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Long >  , JpaSpecificationExecutor<EventType> {
    @Query("SELECT e.eventType FROM Event e WHERE e.id = :eventId")
    Optional<EventType> findByEventId(@Param("eventId") Long eventId);
}