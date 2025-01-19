package com.team25.event.planner.common.controller;

import com.team25.event.planner.common.dto.*;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.common.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Objects;


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping()
    public Page<ReviewResponseDTO> getReviews(@RequestParam(defaultValue = "APPROVED") String status,
                                              @RequestParam(required = false, defaultValue = "false") boolean eventsReviews,
                                              @RequestParam(required = false, defaultValue = "false") boolean offeringsReviews,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(defaultValue = "createdDate") String sortBy,
                                              @RequestParam(defaultValue = "desc") String sortDirection){
        if(eventsReviews){
            return reviewService.getEventReviewsByOrganizer(page,size,sortBy,sortDirection);
        }else if (offeringsReviews) {
            return reviewService.getOfferingReviewsByOwner(page,size,sortBy,sortDirection);
        }else if(eventsReviews == false && offeringsReviews == false) {
            ReviewFilterDTO filterDTO = new ReviewFilterDTO();
            filterDTO.setStatus(getStatus(status));
            return reviewService.getReviews(filterDTO, page, size, sortBy, sortDirection);
        }else{
            return new PageImpl<>(Collections.emptyList());
        }

    }

    private ReviewStatus getStatus(String status){
        if(Objects.equals(status, "APPROVED")){
            return ReviewStatus.APPROVED;
        }else if (Objects.equals(status, "DENIED")) {
            return ReviewStatus.DENIED;
        }else {
            return ReviewStatus.PENDING;
        }
    }

    @PostMapping()
    public ReviewResponseDTO createReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        return reviewService.createReview(reviewRequestDTO);
    }

    @PutMapping(value = "/{id}")
    public ReviewResponseDTO updateReview(@PathVariable Long id, @RequestBody ReviewUpdateRequestDTO reviewRequestDTO){
        return reviewService.updateReview(reviewRequestDTO, id);
    }

    @GetMapping("/event/{eventId}/stats")
    public ResponseEntity<ReviewStatsResponseDTO> getEventReviewStats(@PathVariable Long eventId) {
        return ResponseEntity.ok(reviewService.getEventReviewStats(eventId));
    }
}
