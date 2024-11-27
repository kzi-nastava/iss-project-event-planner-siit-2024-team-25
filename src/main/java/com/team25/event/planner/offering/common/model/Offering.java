package com.team25.event.planner.offering.common.model;

import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.user.model.Owner;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@ToString
public abstract class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private double price;

    private double discount;

    @ElementCollection
    @Column(name = "images_url")
    private List<String> images;


    private boolean isVisible;
    private boolean isAvailable;
    private OfferingType status;

    @ManyToMany
    @ToString.Exclude
    private Collection<EventType> eventType;

    @ManyToOne
    private OfferingCategory offeringCategory;

    @NotNull(message = "Owner is required")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Owner owner;
}
