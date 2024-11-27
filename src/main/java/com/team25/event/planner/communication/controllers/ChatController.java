package com.team25.event.planner.communication.controllers;

import com.team25.event.planner.communication.dto.ChatResponseDTO;
import com.team25.event.planner.communication.dto.UserToOrganizerRequestDTO;
import com.team25.event.planner.communication.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponseDTO> initiateChat(@Valid @RequestBody UserToOrganizerRequestDTO requestDTO)
    {
        return new ResponseEntity<ChatResponseDTO>(chatService.initiateChat(requestDTO.getEventId(), requestDTO.getSenderId(), requestDTO.getOrganizerId()), HttpStatus.CREATED);
    }
}
