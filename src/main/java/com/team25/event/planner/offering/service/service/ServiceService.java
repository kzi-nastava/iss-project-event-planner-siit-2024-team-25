package com.team25.event.planner.offering.service.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.dto.ServiceCreateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceCreateResponseDTO;
import com.team25.event.planner.offering.service.mapper.ServiceMapper;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.MappingControl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final OfferingMapper offeringMapper;
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public ServiceCreateResponseDTO createService(ServiceCreateRequestDTO requestDTO){

        com.team25.event.planner.offering.service.model.Service service = serviceMapper.toEntity(requestDTO);

        if(service.getOfferingCategory() == null){
            service.setStatus(OfferingType.PENDING);
        }else{
            service.setStatus(OfferingType.ACCEPTED);
        }

        service = serviceRepository.save(service);
        return serviceMapper.toDTO(service);

    }

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
