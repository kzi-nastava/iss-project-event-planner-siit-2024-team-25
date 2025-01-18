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
                                              @RequestParam(required = false) Long eventId,
                                              @RequestParam(required = false) Long offeringId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(defaultValue = "createdDate") String sortBy,
                                              @RequestParam(defaultValue = "desc") String sortDirection){
        if(eventId != null && offeringId == null){
            return reviewService.getReviewsByEvent(eventId,page,size,sortBy,sortDirection);
        }else if (offeringId != null && eventId == null) {
            return reviewService.getReviewsByOffering(offeringId,page,size,sortBy,sortDirection);
        }else if(eventId == null) {
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
