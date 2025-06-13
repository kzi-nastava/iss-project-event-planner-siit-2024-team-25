package com.team25.event.planner.offering.service.mapper;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.event.model.EventType;
import com.team25.event.planner.event.repository.EventTypeRepository;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.service.dto.ServiceCreateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceCreateResponseDTO;
import com.team25.event.planner.offering.service.model.Service;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.service.CurrentUserService;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceMapperHelper {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final EventTypeRepository eventTypeRepository;
    private final OfferingCategoryRepository offeringCategoryRepository;

    public ServiceMapperHelper(
            CurrentUserService currentUserService, UserRepository userRepository,
            EventTypeRepository eventTypeRepository,
            OfferingCategoryRepository offeringCategoryRepository
    ) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.offeringCategoryRepository = offeringCategoryRepository;
    }

    @AfterMapping
    public void addFavoriteFlag(@MappingTarget ServiceCreateResponseDTO dto, Service service) {
        User currentUser = currentUserService.getCurrentUser();
        dto.setIsFavorite(currentUser != null && currentUser.getFavoriteServices().contains(service));

    }

    @AfterMapping
    public void mapToService(
            @MappingTarget com.team25.event.planner.offering.service.model.Service service,
            ServiceCreateRequestDTO dto
    ) {
        service.setOwner(getOwner(dto.getOwnerId()));
        service.setEventTypes(getEventTypes(dto.getEventTypesIDs()));
        service.setOfferingCategory(getOfferingCategory(dto.getOfferingCategoryID()));
    }

    private Owner getOwner(Long id) {
        //owner repo
        return userRepository.findById(id)
                .map(user -> (Owner) user)
                .orElseThrow(() -> new InvalidRequestError("Owner not found"));
    }

    private List<EventType> getEventTypes(List<Long> ids) {
        return eventTypeRepository.findAllById(ids);
    }

    private OfferingCategory getOfferingCategory(Long id) {
        return id != null ? offeringCategoryRepository.findById(id).orElse(null) : null;
    }
}
