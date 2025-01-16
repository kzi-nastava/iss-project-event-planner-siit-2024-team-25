package com.team25.event.planner.common.service;

import com.team25.event.planner.common.dto.ReviewRequestDTO;
import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.mapper.ReviewMapper;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.common.repository.ReviewRepository;
import com.team25.event.planner.common.specification.ReviewSpecification;
import com.team25.event.planner.offering.common.dto.ReviewFilterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewSpecification reviewSpecification;
    private final ReviewMapper reviewMapper;

    public Page<ReviewResponseDTO> getReviews() {

    }

    public ReviewResponseDTO updateReviewStatus(@Valid ReviewRequestDTO requestDTO) {

    }
}
