package com.team25.event.planner.offering.common.model;

import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.event.model.Purchase;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "offering_review")
public class OfferingReview extends Review {
    public OfferingReview(Long id, String comment, int rating, ReviewStatus status, Purchase purchase) {
        super(id, comment, rating, status, purchase);
    }
}
