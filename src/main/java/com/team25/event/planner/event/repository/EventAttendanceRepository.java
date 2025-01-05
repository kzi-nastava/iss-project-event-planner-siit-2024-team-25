package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventAttendance;
import com.team25.event.planner.event.model.EventAttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, EventAttendanceId>, JpaSpecificationExecutor<EventAttendance> {

    List<EventAttendance> findByEventId(Long entityId);

    Optional<EventAttendance> getEventAttendanceByAttendeeIdAndEventId(Long userId, Long eventId);
}
