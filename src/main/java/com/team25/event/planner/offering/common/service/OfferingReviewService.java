package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.offering.common.dto.OfferingReviewRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingReviewResponseDTO;
import com.team25.event.planner.offering.common.model.OfferingReview;
import com.team25.event.planner.offering.common.repository.OfferingReviewRepository;
import com.team25.event.planner.offering.service.dto.ServiceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferingReviewService {

    private final OfferingReviewRepository offeringReviewRepository;

    public Collection<OfferingReviewResponseDTO> getOfferingReviews(Long offeringId) {
        //Collection<OfferingReview> offeringReviews = offeringReviewRepository.findAll(); repo
        Collection<OfferingReviewResponseDTO> responseDTOs = new ArrayList<>();

        OfferingReviewResponseDTO review1 = new OfferingReviewResponseDTO(
                1L,
                offeringId,
                5,
                "Great",
                ReviewStatus.PENDING
        );

        OfferingReviewResponseDTO review2 = new OfferingReviewResponseDTO(
                2L,
                offeringId,
                3,
                "Excellent",
                ReviewStatus.PENDING
        );

        responseDTOs.add(review1);
        responseDTOs.add(review2);
        return responseDTOs;

    }

    public OfferingReviewResponseDTO getOfferingReview(Long offeringId, Long reviewId) {
        //FindAllByOfferingId
        //FilterByReviewId
        OfferingReviewResponseDTO review1 = new OfferingReviewResponseDTO(
                reviewId,
                offeringId,
                5,
                "Great",
                ReviewStatus.PENDING
        );
        return review1;
    }

    public OfferingReviewResponseDTO createOfferingReview(Long offeringId, OfferingReviewRequestDTO requestDTO){
        //Optional<OfferingReview> offeringReviewRepo = offeringReviewRepository.findById(offeringId);
        //OfferingReview offeringReview = offeringReviewRepo.orElseThrow(() -> new RuntimeException("Offering review not found"));

        if(!Objects.equals(offeringId, requestDTO.getOfferingId())){
            throw new InvalidRequestError("Offering id must be equal to the request id");
        }
        if(requestDTO.getRate() <= 0 || requestDTO.getRate() > 5){
            throw new InvalidRequestError("Rate must be between 1 and 5");
        }

        OfferingReviewResponseDTO responseDTO = new OfferingReviewResponseDTO(2L,offeringId, requestDTO.getRate(),requestDTO.getComment(), ReviewStatus.PENDING);

        return responseDTO;
    }

    public ResponseEntity<?> deleteReview(Long offeringId, Long reviewId) {
        //FindAllByOfferingId
        //DeleteFoundByReviewId

        OfferingReviewResponseDTO review1 = new OfferingReviewResponseDTO(
                1L,
                offeringId,
                5,
                "Great",
                ReviewStatus.PENDING
        );
        if (!Objects.equals(review1.getId(), reviewId)) { // test, just review with id 1 can be deleted
            return new ResponseEntity<ServiceResponseDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
