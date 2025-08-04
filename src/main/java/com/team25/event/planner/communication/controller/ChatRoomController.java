package com.team25.event.planner.communication.controller;

import com.team25.event.planner.communication.dto.ChatRoomResponseDTO;
import com.team25.event.planner.communication.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats/")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("{senderId}")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #senderId")
    public ResponseEntity<Page<ChatRoomResponseDTO>> getChatsBySender(@PathVariable("senderId")Long senderId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "id", required = false) String sortBy,
                                                                      @RequestParam(defaultValue = "desc") String sortDirection){
        return ResponseEntity.ok(chatRoomService.getChatsBySender(senderId,page,size,sortBy,sortDirection));
    }
}
