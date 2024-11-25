package com.team25.event.planner.offering.common.model;

import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.model.Service;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    private Event event;

    // Attribute should not be mapped super class
    @ManyToOne
    private Product product;

    @ManyToOne
    private Service service;


}
