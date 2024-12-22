package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e WHERE " +
            "(:country IS NULL OR :country = '' OR e.location.country = :country) AND " +
            "(:city IS NULL OR :city = '' OR e.location.city = :city) " +
            "AND (e.privacyType =  com.team25.event.planner.event.model.PrivacyType.PUBLIC )" +
            "ORDER BY e.createdDate DESC")
    Page<Event> findTopEvents(@Param("country") String country,

                              @Param("city") String city,
                              Pageable pageable);

    @Query("""
                SELECT CASE
                           WHEN COUNT(e) = 0 THEN TRUE
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

}