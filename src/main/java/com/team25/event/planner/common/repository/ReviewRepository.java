package com.team25.event.planner.common.repository;

import com.team25.event.planner.common.dto.ReviewResponseDTO;

import com.team25.event.planner.common.dto.RatingCountDTO;

import com.team25.event.planner.common.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("select r.rating as rating, COUNT(r) as count from Review r " +
            "where r.purchase.event.id = :eventId " +
            "and r.reviewStatus = com.team25.event.planner.common.model.ReviewStatus.APPROVED " +
            "group by r.rating order by r.rating asc")
    List<RatingCountDTO> getRatingCountsByEvent(@Param("eventId") Long eventId);

    @Query("select AVG(r.rating) from Review r " +
            "where r.purchase.event.id = :eventId " +
            "and r.reviewStatus = com.team25.event.planner.common.model.ReviewStatus.APPROVED")
    Double getAverageRatingByEvent(@Param("eventId") Long eventId);
}
