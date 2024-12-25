package com.team25.event.planner.event.specification;

import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.service.model.Service;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class PurchaseSpecification {

    public Specification<Purchase> createServiceSpecification(PurchaseServiceRequestDTO requestDTO, Long serviceId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(serviceId != null) {
                predicates.add(cb.equal(root.get("offering").get("id"), serviceId));
            }

            if (requestDTO.getStartDate() != null && requestDTO.getEndDate() != null) {
                if (requestDTO.getStartTime() != null && requestDTO.getEndTime() != null) {
                    Predicate reservationOverlaps = cb.or(
                            cb.and(
                                    cb.greaterThanOrEqualTo(root.get("startDate"), requestDTO.getStartDate()),
                                    cb.lessThanOrEqualTo(root.get("startDate"), requestDTO.getEndDate()),
                                    cb.greaterThanOrEqualTo(root.get("startTime"), requestDTO.getStartTime()),
                                    cb.lessThanOrEqualTo(root.get("startTime"), requestDTO.getEndTime())
                            ),
                            cb.and(
                                    cb.greaterThanOrEqualTo(root.get("endDate"), requestDTO.getStartDate()),
                                    cb.lessThanOrEqualTo(root.get("endDate"), requestDTO.getEndDate()),
                                    cb.greaterThanOrEqualTo(root.get("endTime"), requestDTO.getStartTime()),
                                    cb.lessThanOrEqualTo(root.get("endTime"), requestDTO.getEndTime())
                            ),
                            cb.and(
                                    cb.lessThanOrEqualTo(root.get("startDate"), requestDTO.getStartDate()),
                                    cb.greaterThanOrEqualTo(root.get("endDate"), requestDTO.getEndDate()),
                                    cb.lessThanOrEqualTo(root.get("startTime"), requestDTO.getStartTime()),
                                    cb.greaterThanOrEqualTo(root.get("endTime"), requestDTO.getEndTime())
                            )
                    );
                    predicates.add(reservationOverlaps);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
