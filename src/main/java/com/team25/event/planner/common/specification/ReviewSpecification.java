package com.team25.event.planner.common.specification;

import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.offering.common.dto.ReviewFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewSpecification {
    public Specification<Review> createspecification(ReviewFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(cb.like(root.get("status"),filter.getStatus().toString()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
