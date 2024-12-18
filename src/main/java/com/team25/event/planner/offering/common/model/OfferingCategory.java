package com.team25.event.planner.offering.common.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "OfferingType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferingCategory {

    public OfferingCategory(String name, String description, OfferingCategoryType status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required")
    @Column(nullable = false)
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private OfferingCategoryType status;
}
