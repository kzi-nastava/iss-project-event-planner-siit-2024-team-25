package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.OfferingCategoryCreateRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryUpdateRequestDTO;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.service.OfferingCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/offering-categories/")
public class OfferingCategoryController {
    private final OfferingCategoryService offeringCategoryService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OfferingCategoryResponseDTO> getOfferingCategories() {
        return offeringCategoryService.getOfferingCategories();
    }

    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OfferingCategoryPreviewResponseDTO> getAllOfferingCategories() {
        return offeringCategoryService.getAllOfferingCategories();
    }

    @GetMapping(value = "submitted", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public List<OfferingCategoryResponseDTO> getSubmittedOfferingCategories() {
        return offeringCategoryService.getSubmittedOfferingCategories();
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OfferingCategoryResponseDTO getOfferingCategory(@PathVariable("id") Long id) {
        return offeringCategoryService.getOfferingCategory(id);
    }

    @GetMapping(value = "/submitted/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public OfferingCategoryResponseDTO getSubmittedOfferingCategory(@PathVariable("id") Long id) {
        return offeringCategoryService.getOfferingCategorySubmitted(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public OfferingCategoryResponseDTO createOfferingCategory(@RequestBody OfferingCategoryCreateRequestDTO offeringCategoryCreateRequestDTO){
        return offeringCategoryService.createOfferingCategory(offeringCategoryCreateRequestDTO, OfferingCategoryType.ACCEPTED);
    }

    @PostMapping(value = "submitted", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_OWNER")
    public OfferingCategoryResponseDTO createSubmittedOfferingCategory(@RequestBody OfferingCategoryCreateRequestDTO offeringCategoryCreateRequestDTO){
        return offeringCategoryService.createOfferingCategory(offeringCategoryCreateRequestDTO, OfferingCategoryType.PENDING);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public OfferingCategoryResponseDTO updateOfferingCategory(@PathVariable("id") Long id ,@RequestBody OfferingCategoryUpdateRequestDTO offeringCategoryUpdateRequestDTO){
        return offeringCategoryService.updateOfferingCategory(id, offeringCategoryUpdateRequestDTO);
    }

    @DeleteMapping(value = "{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> deleteOfferingCategory(@PathVariable("id") Long id) {
        return offeringCategoryService.deleteOfferingCategory(id);
    }
}
