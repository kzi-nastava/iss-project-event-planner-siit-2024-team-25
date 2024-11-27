package com.team25.event.planner.offering.product.service;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.product.dto.ProductRequestDTO;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.model.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final OfferingMapper offeringMapper;

    public Page<OfferingPreviewResponseDTO> getProducts(OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        return getMockList();

//        Specification<Offering> spec = offeringSpecificarion.createSpecification(filter);
//        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        return offeringRepository.findAll(spec, pageable).map(offeringMapper::toDTO);
    }

    private Page<OfferingPreviewResponseDTO> getMockList() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setDescription("Description 1");
        product.setPrice(1500);
        List<OfferingPreviewResponseDTO> offerings = new ArrayList<>();
        offerings.add(offeringMapper.toDTO(product));
        return new PageImpl<>(offerings);
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
