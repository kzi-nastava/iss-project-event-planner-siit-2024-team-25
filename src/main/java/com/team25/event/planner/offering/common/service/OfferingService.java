package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.model.ReviewStatus;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Money;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.event.repository.PurchaseRepository;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingReview;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.common.repository.OfferingReviewRepository;
import com.team25.event.planner.offering.common.specification.OfferingSpecification;
import com.team25.event.planner.offering.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    private final PurchaseRepository purchaseRepository;
    private final OfferingReviewRepository offeringReviewRepository;
    private final EventRepository eventRepository;

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



    private Page<OfferingPreviewResponseDTO> getMockList(){

        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setDescription("Description 1");
        product.setPrice(1500);

        com.team25.event.planner.offering.service.model.Service service = new com.team25.event.planner.offering.service.model.Service();
        service.setId(2L);
        service.setName("Service 1");
        service.setDescription("Description 2");
        service.setPrice(1500);
        List<OfferingPreviewResponseDTO> offerings = new ArrayList<>();
        offerings.add(offeringMapper.toDTO(product));
        offerings.add(offeringMapper.toDTO(service));
        return new PageImpl<>(offerings);
    }


}
