package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Money;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingSubmittedResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingReview;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.common.repository.OfferingReviewRepository;
import com.team25.event.planner.offering.common.specification.OfferingSpecification;
import com.team25.event.planner.offering.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingMapper offeringMapper;
    private final OfferingRepository offeringRepository;
    private final OfferingSpecification offeringSpecification;
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final OfferingReviewRepository offeringReviewRepository;
    private final EventRepository eventRepository;

    public List<OfferingSubmittedResponseDTO> getSubmittedOfferings(){
        return offeringRepository.getOfferingSubmittedResponseDTOs();
    }

    @Transactional
    public void updateOfferingsCategory(Long offeringId, Long categoryId, Long updateCategoryId){
        Offering offering = offeringRepository.findById(offeringId).orElseThrow(()-> new NotFoundError("Offering not found"));
        OfferingCategory category = offeringCategoryRepository.findById(categoryId).orElseThrow(()-> new NotFoundError("Submitted category not found"));
        OfferingCategory categoryUpdate = offeringCategoryRepository.findById(updateCategoryId).orElseThrow(()-> new NotFoundError("Category to update not found"));
        offering.setStatus(OfferingType.ACCEPTED);
        offering.setOfferingCategory(categoryUpdate);
        offeringRepository.save(offering);
        if(offeringRepository.countOfferingsByOfferingCategoryId(categoryId) == 0){
            offeringCategoryRepository.deleteById(categoryId);
        }

    }

    public Page<OfferingPreviewResponseDTO> getOfferings(OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Offering> spec = offeringSpecification.createSpecification(filter);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Offering> offeringPage = offeringRepository.findAll(spec, pageable);
        System.out.println(offeringPage.getContent());
        pageable = PageRequest.of(0,size, Sort.by(direction, sortBy));
        List<OfferingPreviewResponseDTO> offeringsWithRatings = offeringRepository.findOfferingsWithAverageRating(offeringPage.getContent(), pageable);
        return new PageImpl<>(offeringsWithRatings, pageable, offeringPage.getTotalElements());
    }
    public Page<OfferingPreviewResponseDTO> getTopOfferings(String country, String city) {
        Pageable pageable = PageRequest.of(0, 5);
        return offeringRepository
                .findTopOfferings(country, city, pageable);
    }
}
