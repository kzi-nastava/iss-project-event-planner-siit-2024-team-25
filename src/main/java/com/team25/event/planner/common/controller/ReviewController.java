package com.team25.event.planner.common.controller;

import com.team25.event.planner.common.dto.ReviewRequestDTO;
import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.repository.ReviewRepository;
import com.team25.event.planner.common.service.ReviewService;
import com.team25.event.planner.offering.common.dto.ReviewFilterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ReviewResponseDTO>> getOfferingReviews(
            @ModelAttribute ReviewFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return new ResponseEntity<>(reviewService.getReviews(filter, page, size, sortBy, sortDirection), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<ReviewResponseDTO> updateReviewStatus(
            @Valid @RequestBody ReviewRequestDTO requestDTO
            ){
        return ResponseEntity.ok(reviewService.updateReviewStatus(requestDTO));
    }
}
