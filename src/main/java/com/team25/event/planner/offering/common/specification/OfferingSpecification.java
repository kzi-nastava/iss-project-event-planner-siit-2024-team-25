package com.team25.event.planner.offering.common.specification;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.model.Offering;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OfferingSpecification {
    public Specification<Offering> createSpecification(OfferingFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getDescription() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getEventTypeId() != null) {
                Join<Object, Object> eventTypeJoin = root.join("eventTypes");
                predicates.add(cb.equal(eventTypeJoin.get("id"), filter.getEventTypeId()));
            }
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("offeringCategory"), filter.getCategoryId()));
            }
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }
            if (filter.getIsAvailable() != null) {
                predicates.add(cb.equal(root.get("isAvailable"), filter.getIsAvailable()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
