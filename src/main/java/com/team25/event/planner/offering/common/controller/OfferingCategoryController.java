package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.OfferingCategoryCreateRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryUpdateRequestDTO;
import com.team25.event.planner.offering.common.service.OfferingCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OfferingCategoryResponseDTO getOfferingCategory(@PathVariable("id") Long id) {
        return offeringCategoryService.getOfferingCategory(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OfferingCategoryResponseDTO createOfferingCategory(@RequestBody OfferingCategoryCreateRequestDTO offeringCategoryCreateRequestDTO){
        return offeringCategoryService.createOfferingCategory(offeringCategoryCreateRequestDTO);
    }

    @PutMapping(value = "{id}/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OfferingCategoryResponseDTO updateOfferingCategory(@PathVariable("id") Long id ,@RequestBody OfferingCategoryUpdateRequestDTO offeringCategoryUpdateRequestDTO){
        return offeringCategoryService.updateOfferingCategory(id, offeringCategoryUpdateRequestDTO);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteOfferingCategory(@PathVariable("id") Long id) {
        return offeringCategoryService.deleteOfferingCategory(id);
    }
}
