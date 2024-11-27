package com.team25.event.planner.communication.model;

import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private EventOrganizer eventOrganizer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
    private List<Message> messages;

}
