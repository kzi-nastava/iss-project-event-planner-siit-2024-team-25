package com.team25.event.planner.offering.common.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private OfferingCategoryType status;
}
