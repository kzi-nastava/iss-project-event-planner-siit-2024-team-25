package com.team25.event.planner.offering.common.specification;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.model.Offering;
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

            if (filter.getEventTypeId() != null) {
                predicates.add(cb.equal(root.get("eventType").get("id"), filter.getEventTypeId()));
            }

            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("offeringCategory"), filter.getCategoryId()));
            }

            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDate()));
            }

            if (filter.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), filter.getStartTime()));
            }

            if (filter.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), filter.getEndTime()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("minPrice"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxPrice"), filter.getMaxPrice()));
            }

            if (filter.getCountry() != null) {
                predicates.add(cb.equal(root.get("location").get("country"), filter.getCountry()));
            }

            if (filter.getCity() != null) {
                predicates.add(cb.equal(root.get("location").get("city"), filter.getCity()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
