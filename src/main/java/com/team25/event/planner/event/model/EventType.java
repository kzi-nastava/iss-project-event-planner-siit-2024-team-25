package com.team25.event.planner.event.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EventType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private boolean isActive;
}
