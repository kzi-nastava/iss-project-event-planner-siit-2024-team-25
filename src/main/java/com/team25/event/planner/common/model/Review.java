package com.team25.event.planner.common.model;

import com.team25.event.planner.event.model.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Comment is required")
    @Column(nullable = false)
    private String comment;

    @NotNull(message = "Rating is required")
    @Column(nullable = false)
    @Min(0)
    @Max(5)
    private int rating;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    @CreationTimestamp
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

}
