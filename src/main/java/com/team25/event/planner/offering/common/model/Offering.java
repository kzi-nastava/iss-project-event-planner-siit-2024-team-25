package com.team25.event.planner.offering.common.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;

    @ElementCollection
    @Column(name = "images_url")
    private List<String> images;

    private boolean isVisible;
    private boolean isAvailable;
    private OfferingType status;

}
