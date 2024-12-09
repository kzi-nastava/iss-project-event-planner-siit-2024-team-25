package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.EventAttendance;
import com.team25.event.planner.event.model.EventAttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, EventAttendanceId>, JpaSpecificationExecutor<EventAttendance> {
}
