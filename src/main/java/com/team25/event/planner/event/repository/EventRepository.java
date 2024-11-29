package com.team25.event.planner.event.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.team25.event.planner.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.stream.Stream;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Page<Event> findAllByOrderByCreatedDateDesc(Pageable pageable);
}