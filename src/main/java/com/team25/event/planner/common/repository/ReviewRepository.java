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
            left outer join Purchase p on r.purchase.id = p.id
	        left outer join Event e on p.event.id = e.id
	        left outer join User u on u.id = e.organizer.id
            where u.id = :userId and r.reviewStatus = 'APPROVED' and r.reviewType = 'EVENT_REVIEW'""")
    Page<ReviewResponseDTO> findEventReviewsByOrganizer(@Param("userId") Long userId, Pageable pageable);

    @Query("""

            select new com.team25.event.planner.common.dto.ReviewResponseDTO(r, o.name) from Review r
            left outer join Purchase p on r.purchase.id = p.id
	        left outer join Offering o on p.offering.id = o.id
	        left outer join User u on u.id = o.owner.id
            where u.id = :userId and r.reviewStatus = 'APPROVED' and r.reviewType = 'OFFERING_REVIEW'""")
    Page<ReviewResponseDTO> findOfferingReviewsByOwner(@Param("userId") Long userId, Pageable pageable);

}
