package com.team25.event.planner.common.service;

import com.team25.event.planner.common.dto.*;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.mapper.ReviewMapper;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.common.repository.ReviewRepository;
import com.team25.event.planner.common.specification.ReviewSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final int MIN_RATING_VALUE = 0;
    private static final int MAX_RATING_VALUE = 5;

    private final ReviewRepository reviewRepository;
    private final ReviewSpecification reviewSpecification;
    private final ReviewMapper reviewMapper;

    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewMapper.reviewFromDTO(reviewRequestDTO);
        review.setReviewStatus(ReviewStatus.PENDING);
        return reviewMapper.toDTO(reviewRepository.save(review));
    }

    public ReviewResponseDTO updateReview(ReviewUpdateRequestDTO reviewRequestDTO, Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(()-> new NotFoundError("Not found review"));
        review.setReviewStatus(reviewRequestDTO.getReviewStatus());
        return reviewMapper.toDTO(reviewRepository.save(review));
    }
    public Page<ReviewResponseDTO> getReviews(ReviewFilterDTO filter,int page, int size, String sortBy, String sortDirection) {
        Specification<Review> specification = reviewSpecification.createspecification(filter);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return reviewRepository.findAll(specification, pageable).map(reviewMapper::toDTO);
    }
    public Page<ReviewResponseDTO> getReviewsByEvent(Long eventId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return reviewRepository.findAllByEvent(eventId, pageable).map(reviewMapper::toDTO);
    }
    public Page<ReviewResponseDTO> getReviewsByOffering(Long offeringId,int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return reviewRepository.findAllByOffering(offeringId, pageable).map(reviewMapper::toDTO);
    }

    public ReviewStatsResponseDTO getEventReviewStats(Long eventId) {
        List<RatingCountDTO> ratingCounts = reviewRepository.getRatingCountsByEvent(eventId);
        Double averageRating = reviewRepository.getAverageRatingByEvent(eventId);

        Map<Integer, Integer> reviewCounts = new HashMap<>();

        for (int i = MIN_RATING_VALUE; i <= MAX_RATING_VALUE; i++) {
            reviewCounts.put(i, 0);
        }

        int reviewCount = 0;
        for (RatingCountDTO row : ratingCounts) {
            reviewCount += row.getCount();
            reviewCounts.put(row.getRating(), row.getCount());
        }

        return new ReviewStatsResponseDTO(reviewCount, averageRating != null ? averageRating : 0.0, reviewCounts);
    }
}
