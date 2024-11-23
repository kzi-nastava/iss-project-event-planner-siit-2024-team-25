package com.team25.event.planner.offering.service.controller;

import com.team25.event.planner.event.dto.EventTypePreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryServiceResponseDTO;
import com.team25.event.planner.offering.service.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/services")
public class ServiceController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ServiceResponseDTO>> getServices() {
        Collection<ServiceResponseDTO> services = SetMockData();

        return new ResponseEntity<Collection<ServiceResponseDTO>>(services, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceResponseDTO> getService(@PathVariable Long id) {
        ServiceResponseDTO service1 = new ServiceResponseDTO();
        service1.setId(1L);
        if (!Objects.equals(service1.getId(), id)) { // if the service exist => update else not found the service
            return new ResponseEntity<ServiceResponseDTO>(HttpStatus.NOT_FOUND);
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

        ArrayList<EventTypePreviewResponseDTO> eventTypes2 = new ArrayList<>();
        EventTypePreviewResponseDTO eventType21 = new EventTypePreviewResponseDTO(3L, "Photography");
        EventTypePreviewResponseDTO eventType22 = new EventTypePreviewResponseDTO(4L, "Videography");
        eventTypes2.add(eventType21);
        eventTypes2.add(eventType22);
        service1.setEventTypes(eventTypes2);

        service1.setOfferingCategory(new OfferingCategoryServiceResponseDTO(1L, "Premium"));

        return new ResponseEntity<ServiceResponseDTO>(service1, HttpStatus.OK);
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

        ArrayList<EventTypePreviewResponseDTO> eventTypes2 = new ArrayList<>();
        EventTypePreviewResponseDTO eventType21 = new EventTypePreviewResponseDTO(3L, "Photography");
        EventTypePreviewResponseDTO eventType22 = new EventTypePreviewResponseDTO(4L, "Videography");
        eventTypes2.add(eventType21);
        eventTypes2.add(eventType22);
        service1.setEventTypes(eventTypes2);

        service1.setOfferingCategory(new OfferingCategoryServiceResponseDTO(1L, "Premium"));

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

        ArrayList<EventTypePreviewResponseDTO> eventTypes = new ArrayList<>();
        EventTypePreviewResponseDTO eventType1 = new EventTypePreviewResponseDTO(3L, "Event Planning");
        EventTypePreviewResponseDTO eventType2 = new EventTypePreviewResponseDTO(4L, "Catering");
        eventTypes.add(eventType1);
        eventTypes.add(eventType2);
        service2.setEventTypes(eventTypes);

        service2.setOfferingCategory(new OfferingCategoryServiceResponseDTO(2L, "Enterprise"));

        services.add(service1);
        services.add(service2);
        return services;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceCreateResponseDTO> createService(@RequestBody ServiceCreateRequestDTO service) throws Exception {
        ServiceCreateResponseDTO service1 = new ServiceCreateResponseDTO();

        service1.setId(10L);
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
        service1.setOfferingCategoryID(service.getOfferingCategoryID());

        return new ResponseEntity<ServiceCreateResponseDTO>(service1, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        ServiceResponseDTO service1 = new ServiceResponseDTO();
        service1.setId(1L);
        if (!Objects.equals(service1.getId(), id)) { // || service1 == null
            return new ResponseEntity<ServiceResponseDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
