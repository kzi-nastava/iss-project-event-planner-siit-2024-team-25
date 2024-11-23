package com.team25.event.planner.event.specification;

import com.team25.event.planner.event.dto.EventFilterDTO;
import com.team25.event.planner.event.model.Event;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventSpecification {
    public Specification<Event> createSpecification(EventFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getNameContains() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.getNameContains().toLowerCase() + "%"));
            }

            if (filter.getDescriptionContains() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + filter.getDescriptionContains().toLowerCase() + "%"));
            }

            if (filter.getEventTypeId() != null) {
                predicates.add(cb.equal(root.get("eventType").get("id"), filter.getEventTypeId()));
            }

            if (filter.getPrivacyType() != null) {
                predicates.add(cb.equal(root.get("privacyType"), filter.getPrivacyType()));
            }

            if (filter.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }

            if (filter.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            if (filter.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), filter.getEndDateFrom()));
            }

            if (filter.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDateTo()));
            }

            if (filter.getStartTimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), filter.getStartTimeFrom()));
            }

            if (filter.getStartTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), filter.getStartTimeTo()));
            }

            if (filter.getMinParticipants() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxParticipants"), filter.getMinParticipants()));
            }

            if (filter.getMaxParticipants() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxParticipants"), filter.getMaxParticipants()));
            }

            if (filter.getCity() != null) {
                predicates.add(cb.equal(root.get("location").get("city"), filter.getCity()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}