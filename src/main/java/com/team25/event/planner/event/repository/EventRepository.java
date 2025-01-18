package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("""
                SELECT CASE
                           WHEN (SELECT COUNT(e1) FROM Event e1 WHERE e1.id = :eventId) = 0 THEN TRUE
                           WHEN e.organizer.id = :userId THEN TRUE
                           WHEN e.privacyType = 0 THEN TRUE
                           WHEN EXISTS (
                               SELECT ea FROM EventAttendance ea
                               WHERE ea.event.id = :eventId AND ea.attendee.id = :userId
                           ) THEN TRUE
                           WHEN EXISTS (
                               SELECT ei FROM EventInvitation ei
                               WHERE ei.event.id = :eventId AND ei.guestEmail = :userEmail
                           ) THEN TRUE
                           ELSE FALSE
                       END
                FROM Event e
                WHERE e.id = :eventId
            """)
    boolean canUserViewEvent(@Param("eventId") Long eventId,
                             @Param("userId") Long userId,
                             @Param("userEmail") String userEmail);

    List<Event> findByOrganizerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long organizerId, LocalDate startDate, LocalDate endDate);
}