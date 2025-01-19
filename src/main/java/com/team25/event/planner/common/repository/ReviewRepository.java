package com.team25.event.planner.common.repository;

import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Query("""
            select new com.team25.event.planner.common.dto.ReviewResponseDTO(r, e.name) from Review r
            left outer join Purchase p on r.purchase.id = p.id left outer join Event e on p.id = e.id
            where e.id = :eventId and r.reviewStatus = 'APPROVED'""")
    Page<ReviewResponseDTO> findAllByEvent(@Param("eventId") Long eventId, Pageable pageable);

    @Query("""
            select new com.team25.event.planner.common.dto.ReviewResponseDTO(r, o.name) from Review r
            left outer join Purchase p on r.purchase.id = p.id left outer join Offering o on p.id = o.id
            where o.id = :offeringId and r.reviewStatus = 'APPROVED'""")
    Page<ReviewResponseDTO> findAllByOffering(@Param("offeringId") Long offeringId, Pageable pageable);

}
