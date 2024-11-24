package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingMapper offeringMapper;

    public Page<OfferingPreviewResponseDTO> getTopOfferings(String country, String city) {
        return getMockList();
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
