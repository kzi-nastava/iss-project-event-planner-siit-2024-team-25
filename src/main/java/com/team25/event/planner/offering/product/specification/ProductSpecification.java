package com.team25.event.planner.offering.product.specification;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.service.CurrentUserService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductSpecification {
    private final CurrentUserService currentUserService;

    public Specification<Product> createSpecification(OfferingFilterDTO filter) {
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

            User currentUser = currentUserService.getCurrentUser();
            if(currentUser != null){
                List<Long> blockedUserIds = currentUser.getBlockedUsers().stream()
                        .map(User::getId)
                        .toList();

                List<Long> blockedByUserIds = currentUser.getBlockedByUsers().stream()
                        .map(User::getId)
                        .toList();

                Subquery<Long> blockedByCurrentUserSubquery = query.subquery(Long.class);
                Root<User> blockedByUserRoot = blockedByCurrentUserSubquery.from(User.class);
                blockedByCurrentUserSubquery.select(blockedByUserRoot.get("id"))
                        .where(cb.and(
                                cb.equal(blockedByUserRoot.get("id"), root.get("owner").get("id")),
                                blockedByUserRoot.get("id").in(blockedUserIds)
                        ));

                Subquery<Long> blockedCurrentUserSubquery = query.subquery(Long.class);
                Root<User> blockedUserRoot = blockedCurrentUserSubquery.from(User.class);
                blockedCurrentUserSubquery.select(blockedUserRoot.get("id"))
                        .where(cb.and(
                                cb.equal(blockedUserRoot.get("id"), root.get("owner").get("id")),
                                root.get("owner").get("id").in(blockedByUserIds)
                        ));

                Predicate notBlockedByCurrentUser = cb.not(cb.exists(blockedByCurrentUserSubquery));
                Predicate notBlockedCurrentUser = cb.not(cb.exists(blockedCurrentUserSubquery));

                predicates.add(cb.and(notBlockedByCurrentUser, notBlockedCurrentUser));
            }

            predicates.add(cb.equal(root.get("isAvailable"), true));
            predicates.add(cb.equal(root.get("deleted"), false));
            predicates.add(getVisiblePredicate(root, cb));
            predicates.add(cb.equal(root.get("status"), OfferingType.ACCEPTED));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate getVisiblePredicate(Root<Product> root, CriteriaBuilder cb) {
        final Long currentUserId = currentUserService.getCurrentUserId();
        return cb.or(cb.equal(root.get("owner").get("id"), currentUserId), cb.equal(root.get("isVisible"), true));
    }
}
