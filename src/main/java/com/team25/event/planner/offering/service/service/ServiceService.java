package com.team25.event.planner.offering.service.service;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final OfferingMapper offeringMapper;


    public Page<OfferingPreviewResponseDTO> getServices(OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        return getMockList();

//        Specification<Offering> spec = offeringSpecificarion.createSpecification(filter);
//        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        return offeringRepository.findAll(spec, pageable).map(offeringMapper::toDTO);
    }

    private Page<OfferingPreviewResponseDTO> getMockList(){

        com.team25.event.planner.offering.service.model.Service service = new com.team25.event.planner.offering.service.model.Service();
        service.setId(2L);
        service.setName("Service 1");
        service.setDescription("Description 2");
        service.setPrice(1500);
        List<OfferingPreviewResponseDTO> offerings = new ArrayList<>();
        offerings.add(offeringMapper.toDTO(service));
        return new PageImpl<>(offerings);
    }
}
