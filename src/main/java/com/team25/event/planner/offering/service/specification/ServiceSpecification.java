package com.team25.event.planner.offering.service.specification;

import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.offering.service.dto.ServiceFilterDTO;
import com.team25.event.planner.offering.service.model.Service;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceSpecification {
    public Specification<Service> createSpecification(ServiceFilterDTO serviceFilterDTO) {
        return (root, query, cb) ->{
            List<Predicate> predicates = new ArrayList<>();

            if(serviceFilterDTO.getOwnerId()!=null){
                predicates.add(cb.equal(root.get("owner").get("id"), serviceFilterDTO.getOwnerId()));
            }

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
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), serviceFilterDTO.getPrice()));
            }
            if(serviceFilterDTO.getAvailable()!= null){
                predicates.add(cb.equal(root.get("isAvailable"), serviceFilterDTO.getAvailable()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
