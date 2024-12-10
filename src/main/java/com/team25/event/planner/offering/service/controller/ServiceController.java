package com.team25.event.planner.offering.service.controller;

import com.team25.event.planner.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.service.dto.*;
import com.team25.event.planner.offering.service.model.ReservationType;
import com.team25.event.planner.offering.service.service.ServiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/services")
@AllArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;


    @GetMapping
    public ResponseEntity<Page<ServiceCardResponseDTO>> getServices(
            @ModelAttribute ServiceFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        return ResponseEntity.ok(serviceService.getServices(filter, page, size, sortBy, sortDirection));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<OfferingPreviewResponseDTO>> getAllServices(
            @ModelAttribute OfferingFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        return ResponseEntity.ok(serviceService.getAllServices(filter, page, size, sortBy, sortDirection));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceDetailsResponseDTO> getService(@PathVariable Long id) {
        ServiceDetailsResponseDTO service1 = new ServiceDetailsResponseDTO();
        service1.setId(1L);
        if (!Objects.equals(service1.getId(), id)) { // if the service exist => update else not found the service
            return new ResponseEntity<ServiceDetailsResponseDTO>(HttpStatus.NOT_FOUND);
        }


        service1.setName("Wedding Photography");
        service1.setDescription("Professional photography services for weddings.");
        service1.setPrice(1000.00);
        service1.setDiscount(10.0);
        service1.setAvailable(false);

        ArrayList<String> images2 = new ArrayList<>();
        images2.add("wedding1.jpg");
        images2.add("wedding2.jpg");
        service1.setImages(images2);

        ArrayList<EventTypeServiceResponseDTO> eventTypes2 = new ArrayList<>();
        EventTypeServiceResponseDTO eventType21 = new EventTypeServiceResponseDTO(3L, "Photography");
        EventTypeServiceResponseDTO eventType22 = new EventTypeServiceResponseDTO(4L, "Videography");
        eventTypes2.add(eventType21);
        eventTypes2.add(eventType22);
        service1.setEventTypes(eventTypes2);

        service1.setOfferingCategory(new OfferingCategoryResponseDTO(1L, "Premium", "desc"));

        service1.setReservationType(ReservationType.MANUAL);
        service1.setSpecifics("Specifics service1");
        service1.setDuration(5);
        service1.setCancellationDeadline(2);
        service1.setReservationDeadline(10);

        return new ResponseEntity<ServiceDetailsResponseDTO>(service1, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ServiceResponseDTO>> searchServices(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String offeringCategory,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "available", required = false) Boolean available
    ) {

        Collection<ServiceResponseDTO> services = filter(name, offeringCategory, eventType, price, available);

        return new ResponseEntity<Collection<ServiceResponseDTO>>(services, HttpStatus.OK);
    }

    private Collection<ServiceResponseDTO> filter(String name, String offeringCategory, String eventType, Double price, Boolean available) {
        Collection<ServiceResponseDTO> services = SetMockData();
        if (name != null) {
            services = services.stream()
                    .filter(service -> service.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (offeringCategory != null) {
            services = services.stream()
                    .filter(service -> service.getOfferingCategory().getName().toLowerCase().contains(offeringCategory.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (eventType != null) {
            services = services.stream()
                    .filter(service -> service.getEventTypes().stream()
                            .anyMatch(eventTypeTemp -> eventTypeTemp.getName().toLowerCase().contains(eventType.toLowerCase())))
                    .collect(Collectors.toList());
        }
        if (price != null) {
            services = services.stream()
                    .filter(service -> service.getPrice() == price)
                    .collect(Collectors.toList());
        }

        if (available != null) {
            services = services.stream()
                    .filter(service -> service.isAvailable() == available)
                    .collect(Collectors.toList());
        }
        return services;
    }

    private Collection<ServiceResponseDTO> SetMockData() {
        Collection<ServiceResponseDTO> services = new ArrayList<>();

        ServiceResponseDTO service1 = new ServiceResponseDTO();
        ServiceResponseDTO service2 = new ServiceResponseDTO();

        service1.setId(1L);
        service1.setName("Wedding Photography");
        service1.setDescription("Professional photography services for weddings.");
        service1.setPrice(1000.00);
        service1.setDiscount(10.0);
        service1.setAvailable(false);

        ArrayList<String> images2 = new ArrayList<>();
        images2.add("wedding1.jpg");
        images2.add("wedding2.jpg");
        service1.setImages(images2);

        ArrayList<EventTypeServiceResponseDTO> eventTypes2 = new ArrayList<>();
        EventTypeServiceResponseDTO eventType21 = new EventTypeServiceResponseDTO(3L, "Photography");
        EventTypeServiceResponseDTO eventType22 = new EventTypeServiceResponseDTO(4L, "Videography");
        eventTypes2.add(eventType21);
        eventTypes2.add(eventType22);
        service1.setEventTypes(eventTypes2);

        service1.setOfferingCategory(new OfferingCategoryResponseDTO(1L, "Premium", "desc"));

        service2.setId(2L);
        service2.setName("Corporate Event Planning");
        service2.setDescription("Comprehensive event planning services for corporate events.");
        service2.setPrice(5000.00);
        service2.setDiscount(15.0);
        service2.setAvailable(false);

        ArrayList<String> images = new ArrayList<>();
        images.add("corporate1.jpg");
        images.add("corporate2.jpg");
        images.add("corporate3.jpg");
        service2.setImages(images);

        ArrayList<EventTypeServiceResponseDTO> eventTypes = new ArrayList<>();
        EventTypeServiceResponseDTO eventType1 = new EventTypeServiceResponseDTO(3L, "Event Planning");
        EventTypeServiceResponseDTO eventType2 = new EventTypeServiceResponseDTO(4L, "Catering");
        eventTypes.add(eventType1);
        eventTypes.add(eventType2);
        service2.setEventTypes(eventTypes);

        service2.setOfferingCategory(new OfferingCategoryResponseDTO(2L, "Enterprise", "desc"));

        services.add(service1);
        services.add(service2);
        return services;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_OWNER")
    public ResponseEntity<ServiceCreateResponseDTO> createService(@Valid @RequestBody ServiceCreateRequestDTO serviceDTO) throws Exception {

        return new ResponseEntity<ServiceCreateResponseDTO>(serviceService.createService(serviceDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_OWNER")
    public ResponseEntity<ServiceUpdateResponseDTO> updateService(@PathVariable Long id, @RequestBody ServiceUpdateRequestDTO service) throws Exception {
        ServiceUpdateResponseDTO service1 = new ServiceUpdateResponseDTO();
        service1.setId(1L);
        if (!Objects.equals(service1.getId(), id)) {
            return new ResponseEntity<ServiceUpdateResponseDTO>(HttpStatus.NOT_FOUND);
        }

        service1.setName(service.getName());
        service1.setDescription(service.getDescription());
        service1.setPrice(service.getPrice());
        service1.setDiscount(service.getDiscount());
        service1.setImages(service.getImages());
        service1.setActive(service.isActive());
        service1.setAvailable(service.isAvailable());
        service1.setSpecifics(service.getSpecifics());
        service1.setStatus(service.getStatus());
        service1.setReservationType(service.getReservationType());
        service1.setDuration(service.getDuration());
        service1.setReservationDeadline(service.getReservationDeadline());
        service1.setCancellationDeadline(service.getCancellationDeadline());
        service1.setEventTypesIDs(service.getEventTypesIDs());

        return new ResponseEntity<ServiceUpdateResponseDTO>(service1, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @Secured("ROLE_OWNER")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        ServiceResponseDTO service1 = new ServiceResponseDTO();
        service1.setId(1L);
        if (!Objects.equals(service1.getId(), id)) { // || service1 == null
            return new ResponseEntity<ServiceResponseDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
