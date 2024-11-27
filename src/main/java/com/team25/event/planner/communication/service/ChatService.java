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
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    public Collection<SendMessageResponseDTO> getChat(Long id){
        // with repo
        /*
        * find chat if exist
        * chat would have messages where senderId have two different values for id, one for sender and one for receiver
        * get all messages
        * transform message to DTOs
        * returns list of DTOs
        * finish */

        // prepare data
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Milos");

        EventOrganizer user2 = new EventOrganizer();
        user2.setId(2L);
        user2.setFirstName("Mirko");

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setUser( user1);
        chat.setEventOrganizer(user2);

        if(chat.getId()!= id){
            throw new InvalidRequestError("Error with communication");
        }
        Collection<Message> messages = mockData(chat,user1,user2);
        Collection<SendMessageResponseDTO> responseDTOs = new ArrayList<>();
        for(Message message: messages){
            responseDTOs.add(messageMapper.toDTO(message));
        }

        return responseDTOs;
    }

    public Collection<Message> mockData(Chat chat, User user1, User user2){
        Collection<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(1L);
        message1.setChat(chat);
        message1.setContent("Hello, I have a question about the event.");
        message1.setTimestamp(LocalDateTime.now());
        message1.setSender(user1);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setChat(chat);
        message2.setContent("Of course, feel free to ask.");
        message2.setTimestamp(LocalDateTime.now().plusMinutes(2));
        message2.setSender(user2);

        Message message3 = new Message();
        message3.setId(3L);
        message3.setChat(chat);
        message3.setContent("Thank you! What time does it start?");
        message3.setTimestamp(LocalDateTime.now().plusMinutes(5));
        message3.setSender(user1);

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        return messages;
    }
}
