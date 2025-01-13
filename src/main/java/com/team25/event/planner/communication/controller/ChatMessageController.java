package com.team25.event.planner.communication.controller;

import com.team25.event.planner.communication.dto.ChatMessageRequestDTO;
import com.team25.event.planner.communication.dto.ChatMessageResponseDTO;
import com.team25.event.planner.communication.model.ChatMessage;
import com.team25.event.planner.communication.model.ChatNotification;
import com.team25.event.planner.communication.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageRequestDTO chatMessage) {
        ChatMessageResponseDTO savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverId().toString(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSender().getId(),
                        savedMsg.getReceiver().getId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<Page<ChatMessageResponseDTO>> findChatMessages(@PathVariable Long senderId,
                                                                         @PathVariable Long recipientId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "timestamp", required = false) String sortBy,
                                                                         @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId, page,size,sortBy, sortDirection));
    }
}
