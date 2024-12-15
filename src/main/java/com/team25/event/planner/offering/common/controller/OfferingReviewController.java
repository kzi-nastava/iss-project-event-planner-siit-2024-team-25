package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.OfferingReviewRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingReviewResponseDTO;
import com.team25.event.planner.offering.common.service.OfferingReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/offerings")
public class OfferingReviewController {
    private final OfferingReviewService offeringReviewService;

    @GetMapping(value = "/{offeringId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<OfferingReviewResponseDTO>> getOfferingReviews(@PathVariable("offeringId") Long offeringId) {
        return new ResponseEntity<Collection<OfferingReviewResponseDTO>>(offeringReviewService.getOfferingReviews(offeringId), HttpStatus.OK);
    }

    @GetMapping(value = "/{offeringId}/reviews/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferingReviewResponseDTO> getOfferingReview(@PathVariable("offeringId") Long offeringId,
                                                                       @PathVariable("reviewId") Long reviewId){
        return new ResponseEntity<OfferingReviewResponseDTO>(offeringReviewService.getOfferingReview(offeringId, reviewId), HttpStatus.OK);
    }

    @PostMapping(value = "/{offeringId}/reviews", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<OfferingReviewResponseDTO> createOfferingReview(@PathVariable("offeringId") Long offeringId,
                                                                          @Valid @RequestBody OfferingReviewRequestDTO offeringReviewRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offeringReviewService.createOfferingReview(offeringId,offeringReviewRequestDTO));

    }

    @DeleteMapping(value = "/{offeringId}/reviews/{reviewId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteReview(@PathVariable("offeringId") Long offeringId,
            @PathVariable("reviewId") Long reviewId) {
        return offeringReviewService.deleteReview(offeringId, reviewId);
    }

}
