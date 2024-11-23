package com.team25.event.planner.event.model;

import jakarta.persistence.*;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_types")
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @NotNull(message = "Name is required")
    @Column(nullable = false)
    private String name;


    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean isActive;
}
