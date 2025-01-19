package com.team25.event.planner.common.service;

import com.team25.event.planner.common.dto.ReviewFilterDTO;
import com.team25.event.planner.common.dto.ReviewRequestDTO;
import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.dto.ReviewUpdateRequestDTO;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.mapper.ReviewMapper;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.common.repository.ReviewRepository;
import com.team25.event.planner.common.specification.ReviewSpecification;
import com.team25.event.planner.user.service.AuthService;
import com.team25.event.planner.user.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewSpecification reviewSpecification;
    private final ReviewMapper reviewMapper;
    private final CurrentUserService currentUserService;

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
    public Page<ReviewResponseDTO> getEventReviewsByOrganizer(int page, int size, String sortBy, String sortDirection) {
        Long userId = currentUserService.getCurrentUserId();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return reviewRepository.findEventReviewsByOrganizer(userId, pageable);
    }
    public Page<ReviewResponseDTO> getOfferingReviewsByOwner(int page, int size, String sortBy, String sortDirection) {
        Long userId = currentUserService.getCurrentUserId();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return reviewRepository.findOfferingReviewsByOwner(userId, pageable);
    }
}
