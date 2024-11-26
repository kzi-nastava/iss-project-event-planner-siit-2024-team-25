package com.team25.event.planner.user.specification;

import com.team25.event.planner.event.dto.EventFilterDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.user.dto.ReportFilterDTO;
import com.team25.event.planner.user.model.Report;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportSpecification  {
public Specification<Report> createSpecification(ReportFilterDTO filter) {
    return (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getViewed() != null) {
            predicates.add(cb.equal(root.get("isViewed"), filter.getViewed()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}
}
