package com.team25.event.planner.offering.common.model;

import com.team25.event.planner.offering.event.model.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "OfferingType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferingCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @Enumerated(EnumType.STRING)
    private OfferingCategoryType status;

    @ManyToMany
    private Collection<EventType> eventType;

    @ManyToOne
    private OfferingCategory offeringCategory;
}
