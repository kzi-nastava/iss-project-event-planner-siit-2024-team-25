package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.EventInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventInvitationRepository extends JpaRepository<EventInvitation, Long>, JpaSpecificationExecutor<EventInvitation> {
    Optional<EventInvitation> findEventInvitationByGuestEmailAndInvitationCode(String guestEmail, String invitationCode);

    @Query("SELECT ei FROM EventInvitation ei " +
            "JOIN ei.event e " +
            "WHERE ei.guestEmail = :email AND " +
            "e.organizer.id = :organizerId AND " +
            "(e.startDate > :today OR (e.startDate = :today AND e.startTime > :time))")
    List<EventInvitation> findEventInvitationsForFutureEvents(@Param("organizerId") Long organizerId,
                                                              @Param("email") String email,
                                                              @Param("today") LocalDate today,
                                                              @Param("time") LocalTime time);

}
