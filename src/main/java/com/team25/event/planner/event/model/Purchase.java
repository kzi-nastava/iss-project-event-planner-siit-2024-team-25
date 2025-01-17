package com.team25.event.planner.event.model;

import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.offering.common.model.Offering;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Price  is required")
    @Embedded
    private Money price;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Offering offering;

    @OneToMany(mappedBy = "purchase")
    private List<Review> reviews;
}
