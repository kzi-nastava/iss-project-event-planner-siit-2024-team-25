package com.team25.event.planner.offering.service.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.mapper.OfferingMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.dto.*;
import com.team25.event.planner.offering.service.mapper.ServiceMapper;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import com.team25.event.planner.offering.service.specification.ServiceSpecification;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.service.CurrentUserService;
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
    private final EventTypeRepository eventTypeRepository;
    private final ServiceMapper serviceMapper;
    private final ServiceSpecification serviceSpecification;
    private final OfferingRepository offeringRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Transactional
    public ServiceCreateResponseDTO createService(ServiceCreateRequestDTO requestDTO){

        com.team25.event.planner.offering.service.model.Service service = serviceMapper.toEntity(requestDTO);

        if(service.getOfferingCategory() == null){
            service.setStatus(OfferingType.PENDING);
            OfferingCategory offeringCategory = offeringCategoryRepository.save(new OfferingCategory(requestDTO.getOfferingCategoryName(),"",OfferingCategoryType.PENDING));
            service.setOfferingCategory(offeringCategory);
            notificationService.sendOfferingCategoryNotificationToAdmin(offeringCategory);
        }else{
            service.setStatus(OfferingType.ACCEPTED);
        }

        service = serviceRepository.save(service);
        return serviceMapper.toDTO(service);

    }

    public Page<ServiceCardResponseDTO> getServices(ServiceFilterDTO filter, int page, int size, String sortBy, String sortDirection){
        User user = userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(() -> new NotFoundError("User not found"));
        Account acc = user.getAccount();
        Specification<com.team25.event.planner.offering.service.model.Service> specification = serviceSpecification.createSpecification(filter, acc);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return serviceRepository.findAll(specification, pageable).map(serviceMapper::toCardDTO);
    }

    public Page<OfferingPreviewResponseDTO> getAllServices(OfferingFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<com.team25.event.planner.offering.service.model.Service> spec = serviceSpecification.createSpecification(filter);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Offering> offeringPage = serviceRepository.findAll(spec, pageable).map(service -> (Offering)service);
        pageable = PageRequest.of(0,size, Sort.by(direction, sortBy));
        List<OfferingPreviewResponseDTO> offeringsWithRatings = offeringRepository.findOfferingsWithAverageRating(offeringPage.getContent(), pageable);
        return new PageImpl<>(offeringsWithRatings, pageable, offeringPage.getTotalElements());
    }
    public ServiceCreateResponseDTO getService(Long id){
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));

        return serviceMapper.toDTO(service);
    }

    public ServiceUpdateResponseDTO updateService(Long id, ServiceUpdateRequestDTO requestDTO){
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));


        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setPrice(requestDTO.getPrice());
        service.setDiscount(requestDTO.getDiscount());
        service.setImages(requestDTO.getImages());
        service.setVisible(requestDTO.isVisible());
        service.setAvailable(requestDTO.isAvailable());
        service.setSpecifics(requestDTO.getSpecifics());
        service.setStatus(requestDTO.getStatus());
        service.setReservationType(requestDTO.getReservationType());
        service.setDuration(requestDTO.getDuration());
        service.setReservationDeadline(requestDTO.getReservationDeadline());
        service.setCancellationDeadline(requestDTO.getCancellationDeadline());
        service.setMinimumArrangement(requestDTO.getMinimumArrangement());
        service.setMaximumArrangement(requestDTO.getMaximumArrangement());
        service.setEventTypes(eventTypeRepository.findAllById(requestDTO.getEventTypesIDs()));

        serviceRepository.save(service);
        return serviceMapper.toUpdateDTO(service);
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
