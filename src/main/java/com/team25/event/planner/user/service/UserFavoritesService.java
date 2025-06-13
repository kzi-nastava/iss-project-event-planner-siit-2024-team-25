package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.mapper.EventMapper;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.repository.EventRepository;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.product.dto.ProductRequestDTO;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.product.mapper.ProductMapper;
import com.team25.event.planner.offering.product.model.Product;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.service.dto.ServiceCardResponseDTO;
import com.team25.event.planner.offering.service.mapper.ServiceMapper;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import com.team25.event.planner.user.dto.FavoriteEventRequestDTO;
import com.team25.event.planner.user.dto.FavouriteOfferingDTO;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFavoritesService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final EventMapper eventMapper;
    private final ServiceMapper serviceMapper;
    private final ProductMapper productMapper;

    public List<EventPreviewResponseDTO> getFavoriteEvents(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        return user.getFavoriteEvents().stream().map(eventMapper::toEventPreviewResponseDTO).toList();
    }

    public EventPreviewResponseDTO addFavoriteEvent(Long userId, FavoriteEventRequestDTO requestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

        Event event = eventRepository.findById(requestDTO.getEventId())
                .orElseThrow(() -> new NotFoundError("Event not found"));

        user.getFavoriteEvents().add(event);
        userRepository.save(user);

        return eventMapper.toEventPreviewResponseDTO(event);
    }

    public void removeEventFromFavorites(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        user.getFavoriteEvents().removeIf(event -> event.getId().equals(eventId));
        userRepository.save(user);
    }
    public List<OfferingPreviewResponseDTO> getFavoriteServices(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        List<com.team25.event.planner.offering.service.model.Service> services = user.getFavoriteServices();
        return serviceRepository.findPreviewsForServices(services);
    }
    public List<OfferingPreviewResponseDTO> getFavoriteProducts(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        List<Product> products = user.getFavoriteProducts();
        return productRepository.findPreviewsForServices(products);
    }

    public ServiceCardResponseDTO addFavoriteService(Long userId, FavouriteOfferingDTO requestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

        com.team25.event.planner.offering.service.model.Service service = serviceRepository.findById(requestDTO.getOfferingId())
                .orElseThrow(() -> new NotFoundError("Service not found"));

        user.getFavoriteServices().add(service);
        userRepository.save(user);
        return serviceMapper.toCardDTO(service);
    }
    public ProductResponseDTO addFavoriteProduct(Long userId, FavouriteOfferingDTO requestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        Product product = productRepository.findById(requestDTO.getOfferingId()).orElseThrow(() -> new NotFoundError("Product not found"));
        user.getFavoriteProducts().add(product);
        userRepository.save(user);
        return productMapper.toDTO(product);
    }

    public ResponseEntity<?> removeServiceFromFavorites(Long userId, Long serviceFavId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        user.getFavoriteServices().removeIf(service -> service.getId().equals(serviceFavId));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
    public ResponseEntity<?> removeProductFromFavorites(Long userId, Long productFavId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        user.getFavoriteProducts().removeIf(product -> product.getId().equals(productFavId));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
