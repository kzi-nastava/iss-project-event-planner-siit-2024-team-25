package com.team25.event.planner.common.model;

import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.common.model.Offering;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Comment is required")
    @Column(nullable = false)
    private String comment;

    @NotNull(message = "Rating is required")
    @Column(nullable = false)
    private int rating;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Purchase purchase;

}
