package com.team25.event.planner.communication.repository;

import com.team25.event.planner.communication.model.ChatMessage;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatId(String s);
}
