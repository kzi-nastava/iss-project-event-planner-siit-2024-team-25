package com.team25.event.planner.offering.service.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
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
import com.team25.event.planner.offering.service.dto.*;
import com.team25.event.planner.offering.service.mapper.ServiceMapper;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import com.team25.event.planner.offering.service.specification.ServiceSpecification;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.MappingControl;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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
    private final OfferingCategoryRepository offeringCategoryRepository;
    private final ServiceMapper serviceMapper;
    private final ServiceSpecification serviceSpecification;

    public ServiceCreateResponseDTO createService(ServiceCreateRequestDTO requestDTO){

        com.team25.event.planner.offering.service.model.Service service = serviceMapper.toEntity(requestDTO);

        if(service.getOfferingCategory() == null){
            service.setStatus(OfferingType.PENDING);
            offeringCategoryRepository.save(new OfferingCategory(requestDTO.getOfferingCategoryName(),"",OfferingCategoryType.PENDING));
        }else{
            service.setStatus(OfferingType.ACCEPTED);
        }

        service = serviceRepository.save(service);
        return serviceMapper.toDTO(service);

    }

    public Page<ServiceCardResponseDTO> getServices(ServiceFilterDTO filter, int page, int size, String sortBy, String sortDirection){
        Specification<com.team25.event.planner.offering.service.model.Service> specification = serviceSpecification.createSpecification(filter);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return serviceRepository.findAll(specification, pageable).map(serviceMapper::toCardDTO);
    }

    public ServiceCreateResponseDTO getService(Long id){
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));
        return serviceMapper.toDTO(service);
    }

    public ServiceUpdateResponseDTO updateService(Long id, ServiceUpdateRequestDTO requestDTO){
        com.team25.event.planner.offering.service.model.Service serviceModel = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));
        ServiceUpdateResponseDTO service = serviceMapper.toUpdateDTO(serviceModel);

        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setPrice(requestDTO.getPrice());
        service.setDiscount(requestDTO.getDiscount());
        service.setImages(requestDTO.getImages());
        service.setVisible(requestDTO.isVisible());
        service.setAvailable(requestDTO.isAvailable());
        service.setSpecifics(requestDTO.getSpecifics());
        service.setStatus(service.getStatus());
        service.setReservationType(service.getReservationType());
        service.setDuration(requestDTO.getDuration());
        service.setReservationDeadline(requestDTO.getReservationDeadline());
        service.setCancellationDeadline(requestDTO.getCancellationDeadline());
        service.setMinimumArrangement(requestDTO.getMinimumArrangement());
        service.setMaximumArrangement(requestDTO.getMaximumArrangement());
        service.setEventTypesIDs(requestDTO.getEventTypesIDs());

        com.team25.event.planner.offering.service.model.Service updatedService = serviceMapper.toUpdatedService(service);
        serviceRepository.save(updatedService);
        return serviceMapper.toUpdateDTO(updatedService);
    }

    public ResponseEntity<?> deleteService(Long id){
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));
        if(service.isDeleted()){
            throw new NotFoundError("Service is deleted");
        }
        serviceRepository.delete(service);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
