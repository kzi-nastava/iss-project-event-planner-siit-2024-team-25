package com.team25.event.planner.offering.service.controller;


import com.team25.event.planner.offering.service.dto.*;

import com.team25.event.planner.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;


import com.team25.event.planner.offering.service.service.ServiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/services")
@AllArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;


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



    @GetMapping
    @Secured("ROLE_OWNER")
    public ResponseEntity<Page<ServiceCardResponseDTO>> getServices(
            @ModelAttribute ServiceFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        return ResponseEntity.ok(serviceService.getServices(filter, page, size, sortBy, sortDirection));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceCreateResponseDTO> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getService(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_OWNER")
    public ResponseEntity<ServiceCreateResponseDTO> createService(@Valid @RequestBody ServiceCreateRequestDTO serviceDTO) throws Exception {
        System.out.println(serviceDTO.getName());
        return new ResponseEntity<ServiceCreateResponseDTO>(serviceService.createService(serviceDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_OWNER")
    public ResponseEntity<ServiceUpdateResponseDTO> updateService(@PathVariable Long id, @RequestBody ServiceUpdateRequestDTO serviceDTO) throws Exception {
        return ResponseEntity.ok(serviceService.updateService(id, serviceDTO));
    }

    @DeleteMapping(value = "/{id}")
    @Secured("ROLE_OWNER")
    public ResponseEntity<?> deleteService(@PathVariable Long id)throws Exception {
        return serviceService.deleteService(id);
    }
}
