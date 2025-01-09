package com.team25.event.planner.communication.repository;

import com.team25.event.planner.communication.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
