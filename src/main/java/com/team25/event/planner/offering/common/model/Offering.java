package com.team25.event.planner.offering.common.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)// which inheritance type?
public class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private double price;
    private double discount;

    @ElementCollection
    @Column(name = "images_url")
    private List<String> images;

    private boolean isVisible;
    private boolean isAvailable;
    private OfferingType offeringType;

}
