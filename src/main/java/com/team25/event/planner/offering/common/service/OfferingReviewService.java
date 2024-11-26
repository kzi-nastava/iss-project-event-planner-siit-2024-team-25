package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.offering.common.repository.OfferingReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferingReviewService {

    private final OfferingReviewRepository offeringReviewRepository;
    public boolean newReview(){
        return true;
    }
}
