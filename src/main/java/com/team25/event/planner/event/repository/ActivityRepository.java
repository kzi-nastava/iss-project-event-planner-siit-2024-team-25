package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByEventIdOrderByStartTimeAsc(Long eventId);
}