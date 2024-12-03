package com.team25.event.planner.offering.common.model;

import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.user.model.Owner;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "offerings")
public class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Description is required")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private double price;

    private double discount = 0.0;

    @ElementCollection
    @Column(name = "images_url")
    @NotEmpty(message = "Service images must contain at least one element.")
    private List<String> images;


    private boolean isVisible = false;
    private boolean isAvailable = false;

    @NotNull(message = "Status is required")
    @Column(nullable = false)
    //@Enumerated(EnumType.STRING)
    private OfferingType status;

    @ManyToMany
    @ToString.Exclude
    @NotEmpty(message = "Service must contain at least one event type.")
    private Collection<EventType> eventTypes;

    @ManyToOne
    private OfferingCategory offeringCategory;

    @NotNull(message = "Owner is required")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Owner owner;
}
