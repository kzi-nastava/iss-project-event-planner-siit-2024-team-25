package com.team25.event.planner.offering.service.controller;


import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.offering.service.dto.*;

import com.team25.event.planner.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;


import com.team25.event.planner.offering.service.service.ServiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @PostMapping
    @Secured("ROLE_OWNER")
    public ResponseEntity<ServiceCreateResponseDTO> createService(@Valid @ModelAttribute ServiceCreateRequestDTO serviceDTO) throws Exception {

        return new ResponseEntity<>(serviceService.createService(serviceDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @Secured("ROLE_OWNER")
    public ResponseEntity<ServiceUpdateResponseDTO> updateService(@PathVariable Long id, @ModelAttribute ServiceUpdateRequestDTO serviceDTO) throws Exception {
        return ResponseEntity.ok(serviceService.updateService(id, serviceDTO));
    }

    @DeleteMapping(value = "/{id}")
    @Secured("ROLE_OWNER")
    public ResponseEntity<?> deleteService(@PathVariable Long id)throws Exception {
        return serviceService.deleteService(id);
    }
    @GetMapping("/{serviceId}/images/{imageId}")
    public ResponseEntity<Resource> getServiceImage(@PathVariable Long serviceId, @PathVariable String imageId) {
        try {
            Resource resource = serviceService.getServiceImage(serviceId, imageId);

            String contentType = Files.probeContentType(Path.of(resource.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new ServerError("Could not load image", 500);
        }
    }
}
