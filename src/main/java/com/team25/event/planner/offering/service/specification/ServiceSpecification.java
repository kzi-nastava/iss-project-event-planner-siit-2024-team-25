package com.team25.event.planner.offering.service.specification;

import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.service.dto.ServiceFilterDTO;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceSpecification {
    public Specification<Service> createSpecification(ServiceFilterDTO serviceFilterDTO, Account acc) {
        return (root, query, cb) ->{
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("owner").get("id"), acc.getId()));

            if(serviceFilterDTO.getName() != null){
                predicates.add(cb.like(root.get("name"), "%"+serviceFilterDTO.getName()+"%"));
            }

            if(serviceFilterDTO.getEventTypeId() != null){
                Join<Service, EventType> eventTypeJoin = root.join("eventTypes");
                predicates.add(cb.equal(eventTypeJoin.get("id"), serviceFilterDTO.getEventTypeId()));
            }
            if(serviceFilterDTO.getOfferingCategoryId() != null){
                predicates.add(cb.equal(root.get("offeringCategory").get("id"), serviceFilterDTO.getOfferingCategoryId()));
            }
            if(serviceFilterDTO.getPrice() != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), serviceFilterDTO.getPrice()));
            }
            if(serviceFilterDTO.getAvailable()!= null){
                predicates.add(cb.equal(root.get("isAvailable"), serviceFilterDTO.getAvailable()));
            }

            predicates.add(cb.notEqual(root.get("status"), OfferingType.PENDING));

            predicates.add(cb.isFalse(root.get("deleted")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Service> createSpecification(OfferingFilterDTO serviceFilterDTO, User currentUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (serviceFilterDTO.getName() != null) {
                predicates.add(cb.like(root.get("name"), "%" + serviceFilterDTO.getName() + "%"));
            }

            if (serviceFilterDTO.getEventTypeId() != null) {
                Join<Offering, EventType> eventTypeJoin = root.join("eventTypes");
                predicates.add(cb.equal(eventTypeJoin.get("id"), serviceFilterDTO.getEventTypeId()));
            }

            if (serviceFilterDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("offeringCategory").get("id"), serviceFilterDTO.getCategoryId()));
            }

            if (serviceFilterDTO.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), serviceFilterDTO.getMinPrice()));
            }

            if (serviceFilterDTO.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), serviceFilterDTO.getMaxPrice()));
            }

            if (serviceFilterDTO.getDescription() != null) {
                predicates.add(cb.equal(root.get("description"), serviceFilterDTO.getDescription()));
            }

            if (serviceFilterDTO.getStartDate() != null || serviceFilterDTO.getEndDate() != null || serviceFilterDTO.getStartTime() != null || serviceFilterDTO.getEndTime() != null) {
                query.distinct(true);
                var reservationSubquery = query.subquery(Integer.class);
                var reservationRoot = reservationSubquery.from(Purchase.class);

                List<Predicate> reservationPredicates = new ArrayList<>();

                if (serviceFilterDTO.getStartDate() != null && serviceFilterDTO.getEndDate() != null) {
                    if (serviceFilterDTO.getStartTime() != null && serviceFilterDTO.getEndTime() != null) {
                        Predicate reservationOverlaps = cb.or(
                                cb.and(
                                        cb.greaterThanOrEqualTo(reservationRoot.get("startDate"), serviceFilterDTO.getStartDate()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("startDate"), serviceFilterDTO.getEndDate()),
                                        cb.greaterThanOrEqualTo(reservationRoot.get("startTime"), serviceFilterDTO.getStartTime()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("startTime"), serviceFilterDTO.getEndTime())
                                ),
                                cb.and(
                                        cb.greaterThanOrEqualTo(reservationRoot.get("endDate"), serviceFilterDTO.getStartDate()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("endDate"), serviceFilterDTO.getEndDate()),
                                        cb.greaterThanOrEqualTo(reservationRoot.get("endTime"), serviceFilterDTO.getStartTime()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("endTime"), serviceFilterDTO.getEndTime())
                                ),
                                cb.and(
                                        cb.lessThanOrEqualTo(reservationRoot.get("startDate"), serviceFilterDTO.getStartDate()),
                                        cb.greaterThanOrEqualTo(reservationRoot.get("endDate"), serviceFilterDTO.getEndDate()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("startTime"), serviceFilterDTO.getStartTime()),
                                        cb.greaterThanOrEqualTo(reservationRoot.get("endTime"), serviceFilterDTO.getEndTime())
                                )
                        );
                        reservationPredicates.add(reservationOverlaps);
                    } else {
                        Predicate reservationOverlaps = cb.or(
                                cb.and(
                                        cb.greaterThanOrEqualTo(reservationRoot.get("startDate"), serviceFilterDTO.getStartDate()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("startDate"), serviceFilterDTO.getEndDate())
                                ),
                                cb.and(
                                        cb.greaterThanOrEqualTo(reservationRoot.get("endDate"), serviceFilterDTO.getStartDate()),
                                        cb.lessThanOrEqualTo(reservationRoot.get("endDate"), serviceFilterDTO.getEndDate())
                                ),
                                cb.and(
                                        cb.lessThanOrEqualTo(reservationRoot.get("startDate"), serviceFilterDTO.getStartDate()),
                                        cb.greaterThanOrEqualTo(reservationRoot.get("endDate"), serviceFilterDTO.getEndDate())
                                )
                        );
                        reservationPredicates.add(reservationOverlaps);
                    }
                }
                reservationSubquery.select(reservationRoot.get("offering").get("id"))
                        .where(cb.and(reservationPredicates.toArray(new Predicate[0])));

                predicates.add(cb.not(root.get("id").in(reservationSubquery)));
            }


            if(currentUser != null){
                List<Long> blockedUserIds = currentUser.getBlockedUsers().stream()
                        .map(User::getId)
                        .toList();

                Subquery<Long> blockedByCurrentUserSubquery = query.subquery(Long.class);
                Root<User> blockedByUserRoot = blockedByCurrentUserSubquery.from(User.class);
                blockedByCurrentUserSubquery.select(blockedByUserRoot.get("id"))
                        .where(cb.and(
                                cb.equal(blockedByUserRoot.get("id"), root.get("owner").get("id")),
                                blockedByUserRoot.get("id").in(blockedUserIds)
                        ));

                Predicate notBlockedByCurrentUser = cb.not(cb.exists(blockedByCurrentUserSubquery));

                predicates.add(notBlockedByCurrentUser);
            }

            //predicates.add(cb.equal(root.get("isAvailable"), true));
            predicates.add(cb.equal(root.get("deleted"), false));
            predicates.add(cb.isTrue(root.get("isVisible")));
            predicates.add(cb.equal(root.get("status"), OfferingType.ACCEPTED));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
