package com.team25.event.planner.communication.repository;

import com.team25.event.planner.communication.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE " +
            "(c.sender.id = :id1 AND c.receiver.id = :id2) OR " +
            "(c.sender.id = :id2 AND c.receiver.id = :id1)")
    List<ChatRoom> findChatBetweenUsers(@Param("id1") Long id1, @Param("id2") Long id2);
    Page<ChatRoom> findAllBySenderId(Long senderId, Pageable pageable);

    List<ChatRoom> getChatRoomsByChatId(String chatId);
}
