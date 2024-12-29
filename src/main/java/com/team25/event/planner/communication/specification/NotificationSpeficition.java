package com.team25.event.planner.communication.specification;

import com.team25.event.planner.communication.dto.NotificationFilterDTO;
import com.team25.event.planner.communication.model.Notification;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationSpeficition {
    public Specification<Notification> createSpecification(NotificationFilterDTO filter, User user) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getIsViewed() != null) {
                predicates.add(cb.equal(root.get("isViewed"), filter.getIsViewed()));
            }

            predicates.add(cb.equal(root.get("user"), user));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}