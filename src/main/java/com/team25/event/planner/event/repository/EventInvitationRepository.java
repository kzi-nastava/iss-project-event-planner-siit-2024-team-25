package com.team25.event.planner.event.repository;

import com.team25.event.planner.event.model.EventInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventInvitationRepository extends JpaRepository<EventInvitation, Long>, JpaSpecificationExecutor<EventInvitation> {
}
