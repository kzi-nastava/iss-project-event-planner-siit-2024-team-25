package com.team25.event.planner.communication.controller;

import com.team25.event.planner.communication.dto.SendMessageRequestDTO;
import com.team25.event.planner.communication.dto.SendMessageResponseDTO;
import com.team25.event.planner.communication.service.ChatService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chats")
@AllArgsConstructor
public class SendMessageController {

    private ChatService chatService;

    @PostMapping(value = "/{chatId}/message", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SendMessageResponseDTO> sendMessage(@PathVariable("chatId")Long chatId,
                                        @Valid @RequestBody SendMessageRequestDTO requestDTO){
        SendMessageResponseDTO message = chatService.sendMessage(chatId, requestDTO);
        return ResponseEntity.ok(message);
    }
}
