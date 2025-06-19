package com.team25.event.planner.offering.common.specification;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OfferingSpecification {
    public Specification<Offering> createSpecification(OfferingFilterDTO filter, User currentUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getDescription() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + filter.getDescription().toLowerCase() + "%"));
            }
            if (filter.getEventTypeId() != null) {
                Join<Object, Object> eventTypeJoin = root.join("eventTypes");
                predicates.add(cb.equal(eventTypeJoin.get("id"), filter.getEventTypeId()));
            }
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("offeringCategory").get("id"), filter.getCategoryId()));
            }
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            if (currentUser != null) {

                List<Long> blockedUserIds = currentUser.getBlockedUsers().stream()
                        .map(User::getId)
                        .toList();

                if (!blockedUserIds.isEmpty()) {
                    predicates.add(cb.not(root.get("owner").get("id").in(blockedUserIds)));
                }

                Subquery<Long> blockedByOwnerSubquery = query.subquery(Long.class);
                Root<User> ownerUser = blockedByOwnerSubquery.from(User.class);
                blockedByOwnerSubquery.select(ownerUser.get("id"))
                        .where(
                                cb.equal(ownerUser.get("id"), root.get("owner").get("id")),
                                cb.isMember(currentUser, ownerUser.get("blockedUsers"))
                        );

                predicates.add(cb.not(cb.exists(blockedByOwnerSubquery)));
            }


            predicates.add(cb.equal(root.get("deleted"), false));
            //predicates.add(cb.equal(root.get("isAvailable"), true));
            predicates.add(cb.equal(root.get("isVisible"), true));
            predicates.add(cb.equal(root.get("status"), OfferingType.ACCEPTED));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
