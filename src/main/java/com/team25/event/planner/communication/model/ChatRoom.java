package com.team25.event.planner.communication.model;

import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    private String id;
    private String chatId;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
}
