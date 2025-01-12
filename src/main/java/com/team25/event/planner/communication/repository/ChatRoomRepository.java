package com.team25.event.planner.communication.repository;

import com.team25.event.planner.communication.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Page<ChatRoom> findAllBySenderId(Long senderId, Pageable pageable);
}
