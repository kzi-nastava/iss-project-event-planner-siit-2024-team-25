package com.team25.event.planner.common.mapper;

import com.team25.event.planner.common.dto.ReviewRequestDTO;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.event.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapperHelper {
    private final PurchaseRepository purchaseRepository;

    @AfterMapping
    public void toReview(
            @MappingTarget Review review,
            ReviewRequestDTO reviewRequestDTO
    ){
        review.setPurchase(toPurchase(reviewRequestDTO.getPurchaseId()));
    }

    private Purchase toPurchase(Long id){
        return purchaseRepository.findById(id).orElseThrow(()-> new NotFoundError("Purchase not found"));
    }

}
