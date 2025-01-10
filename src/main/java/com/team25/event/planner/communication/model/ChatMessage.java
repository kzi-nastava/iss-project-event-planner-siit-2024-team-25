package com.team25.event.planner.communication.model;

import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    private String content;
    private Date timestamp;
}
