package com.team25.event.planner.common.repository;

import com.team25.event.planner.common.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Query("""
            select r from Review r
            left outer join Purchase p on r.id = p.id left outer join Event e on p.id = e.id
            where e.id = :eventId and r.reviewStatus = 'APPROVED'""")
    Page<Review> findAllByEvent(@Param("eventId") Long eventId, Pageable pageable);

    @Query("""
            select r from Review r
            left outer join Purchase p on r.id = p.id left outer join Offering o on p.id = o.id
            where o.id = :offeringId and r.reviewStatus = 'APPROVED'""")
    Page<Review> findAllByOffering(@Param("offeringId") Long offeringId, Pageable pageable);

}
