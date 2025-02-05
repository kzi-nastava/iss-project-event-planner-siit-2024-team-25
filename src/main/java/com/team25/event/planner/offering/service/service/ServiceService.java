package com.team25.event.planner.offering.service.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.common.exception.UnauthorizedError;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.common.util.FileUtils;
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
import com.team25.event.planner.offering.product.service.ProductService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ServiceService {

    private final Logger logger = LoggerFactory.getLogger(ServiceService.class);
    private final Path serviceImageFileStorageLocation;
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

    public ServiceService(@Value("${file-storage.images.service}") String serviceImageSaveDirectory,
                          OfferingMapper offeringMapper,
                          ServiceRepository serviceRepository,
                          OfferingCategoryRepository offeringCategoryRepository,
                          EventTypeRepository eventTypeRepository,
                          ServiceMapper serviceMapper,
                          ServiceSpecification serviceSpecification,
                          OfferingRepository offeringRepository,
                          UserRepository userRepository,
                          CurrentUserService currentUserService,
                          NotificationService notificationService) {
        this.serviceImageFileStorageLocation = Paths.get(serviceImageSaveDirectory).toAbsolutePath().normalize();
        this.offeringMapper = offeringMapper;
        this.serviceRepository = serviceRepository;
        this.offeringCategoryRepository = offeringCategoryRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.serviceMapper = serviceMapper;
        this.serviceSpecification = serviceSpecification;
        this.offeringRepository = offeringRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
        try {
            Files.createDirectories(serviceImageFileStorageLocation);
        } catch (IOException e) {
            logger.error("Service images directory creation failed: {}", serviceImageFileStorageLocation);
            throw new ServerError("Failed to store image", 500);
        }
    }


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
        final List<String> imageFiles = saveServiceImages(requestDTO.getImages());
        service.setImages(imageFiles);
        try{
            service = serviceRepository.save(service);
        }catch (Exception e){
            logger.warn("Starting service image cleanup due to failed database write.", e);
            FileUtils.deleteFiles(serviceImageFileStorageLocation, imageFiles);
            throw e;
        }


        return serviceMapper.toDTO(service);

    }

    private List<String> saveServiceImages(List<MultipartFile> images){
        if(images == null){
            return new ArrayList<>();
        }
        List<String> filenames = new ArrayList<>();
        RuntimeException failException = null;
        for(MultipartFile file : images){
            if(!FileUtils.isImage(file)){
                failException = new InvalidRequestError("One or more service images are not valid");
                break;
            }
            final String extension = FileUtils.getExtensionOrDefault(file, "png");
            final String filename = UUID.randomUUID() + "." + extension;
            Path filepath = serviceImageFileStorageLocation.resolve(filename);

            try {
                file.transferTo(filepath.toFile());
            } catch (IOException e) {
                logger.error("Failed to store a service image");
                failException = new ServerError("Failed to store image", 500);
            }

            filenames.add(filename);
        }

        if (failException != null) {
            FileUtils.deleteFiles(serviceImageFileStorageLocation, filenames);
            throw failException;
        }

        return filenames;
    }

    public Resource getServiceImage(Long serviceId, String imageFileName){
        final com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundError("Service not found"));

        if (!service.getImages().contains(imageFileName)) {
            throw new NotFoundError("Image not found");
        }
        Path filePath = serviceImageFileStorageLocation.resolve(imageFileName);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new NotFoundError("Image not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new NotFoundError("Image not found");
        }
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
        User currentUser = currentUserService.getCurrentUser();
        Specification<com.team25.event.planner.offering.service.model.Service> spec = serviceSpecification.createSpecification(filter,currentUser);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Offering> offeringPage = serviceRepository.findAll(spec, pageable).map(service -> (Offering)service);
        pageable = PageRequest.of(0,size, Sort.by(direction, sortBy));
        List<OfferingPreviewResponseDTO> offeringsWithRatings = offeringRepository.findOfferingsWithAverageRating(offeringPage.getContent(), pageable);
        return new PageImpl<>(offeringsWithRatings, pageable, offeringPage.getTotalElements());
    }

    public ServiceCreateResponseDTO getService(Long id){
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));
        User currentUser = currentUserService.getCurrentUser();
        if(service.getOwner().getBlockedUsers().contains(currentUser) || service.getOwner().getBlockedByUsers().contains(currentUser)) {
            throw new UnauthorizedError("You can't see this product page.");
        }
        return serviceMapper.toDTO(service);
    }



    public ServiceUpdateResponseDTO updateService(Long id, ServiceUpdateRequestDTO requestDTO){
        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(id).orElseThrow(()->new NotFoundError("Service not found"));


        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setPrice(requestDTO.getPrice());
        service.setDiscount(requestDTO.getDiscount());

        if(requestDTO.getImagesToDelete()!=null){
            service.getImages().removeAll(requestDTO.getImagesToDelete());
            for (String image : requestDTO.getImagesToDelete()) {
                Path imagePath = serviceImageFileStorageLocation.resolve(image);
                System.out.println("Deleting file: " + imagePath);
                if (!Files.exists(imagePath)) {
                    System.out.println("File does not exist: " + imagePath);
                }
            }
            FileUtils.deleteFiles(serviceImageFileStorageLocation, requestDTO.getImagesToDelete());

        }
        if(requestDTO.getImages() != null){
            final List<String> fileNames = saveServiceImages(requestDTO.getImages());
            service.getImages().addAll(fileNames);
        }

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
