package com.team25.event.planner.communication.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.communication.dto.ChatRoomRequestDTO;
import com.team25.event.planner.communication.dto.ChatRoomResponseDTO;
import com.team25.event.planner.communication.mapper.ChatRoomMapper;
import com.team25.event.planner.communication.model.ChatRoom;
import com.team25.event.planner.communication.repository.ChatRoomRepository;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomMapper chatRoomMapper;

    public Optional<String> getChatRoomId(
            ChatRoomRequestDTO requestDTO) {
        return chatRoomRepository
                .findBySenderIdAndReceiverId(requestDTO.getSenderId(), requestDTO.getReceiverId())
                .map(ChatRoom::getChatId)
                .or(() -> {
                        User sender = userRepository.findById(requestDTO.getSenderId()).orElseThrow(()->new NotFoundError("Sender not found"));
                        User receiver =userRepository.findById(requestDTO.getReceiverId()).orElseThrow(()->new NotFoundError("Receiver not found"));
                        var chatId = createChatId(sender,receiver);
                        return Optional.of(chatId);

                });
    }

    private String createChatId(User sender, User receiver) {
        String chatId = String.format("%s_%s", sender.getId(), receiver.getId());

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .sender(sender)
                .receiver(receiver)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .sender(receiver)
                .receiver(sender)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }

    public Page<ChatRoomResponseDTO> getChatsBySender(Long senderId,int page, int size, String sortBy, String sortDirection){
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return chatRoomRepository.findAllBySenderId(senderId,pageable).map(chatRoomMapper::toChatRoomResponseDTO);
    }
}
