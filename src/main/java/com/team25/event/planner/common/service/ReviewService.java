package com.team25.event.planner.common.service;

import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.mapper.ReviewMapper;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.common.repository.ReviewRepository;
import com.team25.event.planner.common.specification.ReviewSpecification;
import com.team25.event.planner.offering.common.dto.ReviewFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewSpecification reviewSpecification;
    private final ReviewMapper reviewMapper;

    public Page<ReviewResponseDTO> getReviews(ReviewFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
//        Specification<Review> spec = reviewSpecification.createspecification(filter);
//
//        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//
//        return reviewRepository.findAll(spec, pageable).map(reviewMapper::toDTO);
        Review review = new Review();
        review.setId(1L);
        review.setComment("test");
        review.setRating(1);
        ReviewResponseDTO reviewResponseDTO = reviewMapper.toDTO(review);
        ReviewResponseDTO reviewResponseDTO1 = reviewMapper.toDTO(review);

        List<ReviewResponseDTO> reviewResponseDTOList = new ArrayList<>();
        reviewResponseDTOList.add(reviewResponseDTO);
        reviewResponseDTOList.add(reviewResponseDTO1);

        return new PageImpl<>(reviewResponseDTOList);
    }
}
