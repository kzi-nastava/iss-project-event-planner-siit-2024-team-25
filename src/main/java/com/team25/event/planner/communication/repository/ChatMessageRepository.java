package com.team25.event.planner.communication.repository;

import com.team25.event.planner.communication.model.ChatMessage;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chatId like :chatId1 OR m.chatId like :chatId2")
    Page<ChatMessage> findMessagesByChat(@Param("chatId1") String chatId1, @Param("chatId2") String chatId2, Pageable pageable);

}
