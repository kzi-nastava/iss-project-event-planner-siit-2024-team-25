package com.team25.event.planner.event.model;

import com.team25.event.planner.offering.common.model.OfferingCategory;
import jakarta.persistence.*;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @ManyToMany
    private List<OfferingCategory> offeringCategories;
}
