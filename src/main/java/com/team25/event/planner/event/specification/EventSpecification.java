package com.team25.event.planner.event.specification;

import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.event.dto.EventFilterDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.PrivacyType;
import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.model.UserRole;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventSpecification {
    public Specification<Event> createSpecification(EventFilterDTO filter, User currentUser) {
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

            if (filter.getStartDate() != null) {
                predicates.add(cb.equal(root.get("startDate"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                predicates.add(cb.equal(root.get("endDate"), filter.getEndDate()));
            }

            if (filter.getStartTime() != null) {
                predicates.add(cb.equal(root.get("startTime"), filter.getStartTime()));
            }

            if (filter.getEndTime() != null) {
                predicates.add(cb.equal(root.get("endTime"), filter.getEndTime()));
            }

            if (filter.getMaxParticipants() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxParticipants"), filter.getMaxParticipants()));
            }

            if (filter.getCountry() != null) {
                predicates.add(cb.equal(root.get("location").get("country"), filter.getCountry()));
            }

            if (filter.getCity() != null) {
                predicates.add(cb.equal(root.get("location").get("city"), filter.getCity()));
            }

            if(filter.getPrivacyType() == null) {
                predicates.add(cb.equal(root.get("privacyType"), PrivacyType.PUBLIC));
            }

            if (currentUser != null) {

                List<Long> blockedUserIds = currentUser.getBlockedUsers().stream()
                        .map(User::getId)
                        .toList();

                if (!blockedUserIds.isEmpty()) {
                    predicates.add(cb.not(root.get("organizer").get("id").in(blockedUserIds)));
                }

                predicates.add(cb.isNotMember(currentUser, root.get("organizer").get("blockedUsers")));

                if (currentUser.getUserRole() == UserRole.REGULAR) {
                    List<Long> blockedByUserIds = currentUser.getBlockedByUsers().stream()
                            .filter(user -> user.getUserRole() == UserRole.EVENT_ORGANIZER)
                            .map(User::getId)
                            .toList();

                    Subquery<Long> blockedCurrentUserSubquery = query.subquery(Long.class);
                    Root<User> blockedUserRoot = blockedCurrentUserSubquery.from(User.class);
                    blockedCurrentUserSubquery.select(blockedUserRoot.get("id"))
                            .where(cb.and(
                                    cb.equal(blockedUserRoot.get("id"), root.get("organizer").get("id")),
                                    blockedUserRoot.get("id").in(blockedByUserIds)
                            ));

                    Predicate notBlockedCurrentUser = cb.not(cb.exists(blockedCurrentUserSubquery));

                    predicates.add(notBlockedCurrentUser);
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Event> createOrganizerSpecification(EventFilterDTO filter, User organizer) {
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

            if (filter.getMaxParticipants() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxParticipants"), filter.getMaxParticipants()));
            }

            if (filter.getCountry() != null) {
                predicates.add(cb.equal(root.get("location").get("country"), filter.getCountry()));
            }

            if (filter.getCity() != null) {
                predicates.add(cb.equal(root.get("location").get("city"), filter.getCity()));
            }

            predicates.add(cb.equal(root.get("organizer").get("id"), organizer.getId()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Event> createEventNotificationSpecification(LocalDate startDate, LocalTime startTime){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("startDate"), startDate));
            predicates.add(cb.equal(root.get("startTime"), startTime));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Event> createTopEventsSpecification(LocationResponseDTO location, User currentUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(location != null){
                if (location.getCountry() != null) {
                    predicates.add(cb.equal(root.get("location").get("country"), location.getCountry()));
                }

                if (location.getCity() != null) {
                    predicates.add(cb.equal(root.get("location").get("city"), location.getCity()));
                }
            }

            predicates.add(cb.equal(root.get("privacyType"), PrivacyType.PUBLIC));

            if (currentUser != null) {
                List<Long> blockedUserIds = currentUser.getBlockedUsers().stream()
                        .map(User::getId)
                        .toList();

                if (!blockedUserIds.isEmpty()) {
                    predicates.add(cb.not(root.get("organizer").get("id").in(blockedUserIds)));
                }

                predicates.add(cb.isNotMember(currentUser, root.get("organizer").get("blockedUsers")));

                if (currentUser.getUserRole() == UserRole.REGULAR) {
                    List<Long> blockedByUserIds = currentUser.getBlockedByUsers().stream()
                            .filter(user -> user.getUserRole() == UserRole.EVENT_ORGANIZER)
                            .map(User::getId)
                            .toList();

                    Subquery<Long> blockedCurrentUserSubquery = query.subquery(Long.class);
                    Root<User> blockedUserRoot = blockedCurrentUserSubquery.from(User.class);
                    blockedCurrentUserSubquery.select(blockedUserRoot.get("id"))
                            .where(cb.and(
                                    cb.equal(blockedUserRoot.get("id"), root.get("organizer").get("id")),
                                    blockedUserRoot.get("id").in(blockedByUserIds)
                            ));

                    Predicate notBlockedCurrentUser = cb.not(cb.exists(blockedCurrentUserSubquery));

                    predicates.add(notBlockedCurrentUser);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}