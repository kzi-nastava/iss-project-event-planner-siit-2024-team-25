package com.team25.event.planner.communication.service;
import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.communication.dto.ChatResponseDTO;
import com.team25.event.planner.communication.dto.SendMessageRequestDTO;
import com.team25.event.planner.communication.dto.SendMessageResponseDTO;
import com.team25.event.planner.communication.mapper.ChatMapper;
import com.team25.event.planner.communication.mapper.MessageMapper;
import com.team25.event.planner.communication.model.Chat;
import com.team25.event.planner.communication.model.Message;
import com.team25.event.planner.communication.repository.ChatRepository;
import com.team25.event.planner.communication.repository.MessageRepository;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ChatService {

    private final MessageMapper messageMapper;
    private ChatRepository chatRepository;

    private EventRepository eventRepository;

    private MessageRepository messageRepository;

    private ChatMapper chatMapper;

    public ChatResponseDTO initiateChat(Long eventId, Long senderId, Long receiverId) {
        // we still do not have data in db
        /*Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new InvalidRequestError("Event not found"));*/

        /*if (!event.getAttendees().contains(user)) {
            throw new AccessDeniedException("You are not registered for this event.");
        }*/

        EventOrganizer organizer = new EventOrganizer();
        organizer.setId(receiverId);
        organizer.setFirstName("Mirko");

        if(!Objects.equals(organizer.getId(), receiverId)){
            throw new InvalidRequestError("Organizer does not belong to this event");
        }
        User user = new User();
        user.setId(senderId);
        user.setFirstName("Milos");
        // find chat if exist between user and organizer
        /*
        User user = UserRepo.findById(senderId)

        Chat existingChat = chatRepository.findByUserAndOrganizer(user, organizer);
        if (existingChat != null) {
            return existingChat;
        }*/

        Chat chat = new Chat();
        chat.setUser(user);
        chat.setEventOrganizer(organizer);
        //Chat chat = chatRepository.save(newChat);
        return chatMapper.toDTO(chat);
    }

    public SendMessageResponseDTO sendMessage(Long chatId, SendMessageRequestDTO requestDTO){
        // still do not have data in db
        /*Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new InvalidRequestError("Chat not found"));*/

        Chat chat = new Chat();
        chat.setId(1L);
        if(chatId!=chat.getId()){
            throw new InvalidRequestError("Error with communication");
        }
        User user = new User();
        user.setId(2L);
        EventOrganizer organizer = new EventOrganizer();
        organizer.setId(2L);
        chat.setUser(user);
        chat.setEventOrganizer(organizer);

        if (!Objects.equals(chat.getUser().getId(), requestDTO.getSenderId()) || !Objects.equals(chat.getEventOrganizer().getId(), organizer.getId())) {
            throw new InvalidRequestError("You are not a participant in this chat.");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setContent(requestDTO.getMessage());
        User temp = new User();
        temp.setId(requestDTO.getSenderId());
        message.setSender(temp);
        message.setTimestamp(LocalDateTime.now());
        //messageRepository.save(message);
        return messageMapper.toDTO(message);
    }
}
