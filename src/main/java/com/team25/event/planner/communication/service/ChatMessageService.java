package com.team25.event.planner.communication.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.communication.dto.ChatMessageRequestDTO;
import com.team25.event.planner.communication.dto.ChatMessageResponseDTO;
import com.team25.event.planner.communication.dto.ChatRoomRequestDTO;
import com.team25.event.planner.communication.mapper.ChatMessageMapper;
import com.team25.event.planner.communication.model.ChatMessage;
import com.team25.event.planner.communication.repository.ChatMessageRepository;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageResponseDTO save(ChatMessageRequestDTO requestDTO) {
        User sender = userRepository.findById(requestDTO.getSenderId()).orElseThrow(()->new NotFoundError("Sender is not found"));
        User receiver = userRepository.findById(requestDTO.getReceiverId()).orElseThrow(()->new NotFoundError("Receiver is not found"));
        var chatId = chatRoomService
                .getChatRoomId(new ChatRoomRequestDTO(requestDTO.getSenderId(),requestDTO.getReceiverId(),true))
                .orElseThrow(()->new IllegalArgumentException("Chat problem"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setChatId(chatId);
        chatMessage.setContent(requestDTO.getContent());
        chatMessage.setTimestamp(new Date());

        ChatMessage ch = chatMessageRepository.save(chatMessage);
        ChatMessageResponseDTO as = chatMessageMapper.toChatMessageResponseDTO(ch);
        return as;
    }

    public Page<ChatMessageResponseDTO> findChatMessages(Long senderId, Long recipientId, int page, int size, String sortBy, String sortDirection) {
        Optional<String> chatId = chatRoomService.getChatRoomId(new ChatRoomRequestDTO(senderId, recipientId, false));
        if(chatId.isEmpty()){
            throw new NotFoundError("Chat problem");
        }
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ChatMessageResponseDTO> res = chatMessageRepository.findByChatId(chatId.get(), pageable)
                .map(chatMessageMapper::toChatMessageResponseDTO);
        return res;

    }
}
