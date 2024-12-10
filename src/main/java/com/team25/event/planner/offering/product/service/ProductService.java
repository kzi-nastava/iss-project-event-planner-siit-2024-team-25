package com.team25.event.planner.offering.product.service;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.common.specification.OfferingSpecification;
import com.team25.event.planner.offering.product.dto.ProductRequestDTO;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.product.specification.ProductSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final OfferingMapper offeringMapper;
    private final OfferingRepository offeringRepository;
    private final ProductSpecification productSpecification;
    private final ProductRepository productRepository;

    public Page<OfferingPreviewResponseDTO> getAllProducts(OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Product> spec = productSpecification.createSpecification(filter);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Offering> offeringPage = productRepository.findAll(spec, pageable).map(product -> (Offering) product);
        System.out.println(offeringPage.getContent());
        pageable = PageRequest.of(0,size, Sort.by(direction, sortBy));
        List<OfferingPreviewResponseDTO> offeringsWithRatings = offeringRepository.findOfferingsWithAverageRating(offeringPage.getContent(), pageable);
        return new PageImpl<>(offeringsWithRatings, pageable, offeringPage.getTotalElements());
    }

    public ProductResponseDTO createProduct(@Valid ProductRequestDTO productDto) {
        return null;
    }

    public ProductResponseDTO updateProduct(Long id, @Valid ProductRequestDTO productDto) {
        return null;
    }

    public void deleteProduct(Long id) {

    }
}
