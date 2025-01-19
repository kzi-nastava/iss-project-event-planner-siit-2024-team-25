package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.dto.AttendeeProjection;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.EventAttendance;
import com.team25.event.planner.event.model.EventAttendanceId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, EventAttendanceId>, JpaSpecificationExecutor<EventAttendance> {

    List<EventAttendance> findByEventId(Long entityId);

    @Query("select u.id as userId, u.firstName as firstName, u.lastName as lastName " +
            "from EventAttendance ea join ea.attendee u " +
            "where ea.event.id = :eventId")
    Page<AttendeeProjection> findAttendeesByEventId(@Param("eventId") Long eventId, Pageable pageable);

    Optional<EventAttendance> getEventAttendanceByAttendeeIdAndEventId(Long userId, Long eventId);

    @Query("select e from EventAttendance ea join ea.event e " +
            "where ea.attendee.id = :userId " +
            "and e.startDate <= :startDate and e.endDate >= :endDate")
    List<Event> findByAttendeeIdOverlappingDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}
