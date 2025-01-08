package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.OfferingFilterDTO;
import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingSubmittedResponseDTO;
import com.team25.event.planner.offering.common.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offerings")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;

    @GetMapping(value = "submitted", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OfferingSubmittedResponseDTO>> getSubmittedOfferings() {
        return ResponseEntity.ok(offeringService.getSubmittedOfferings());
    }

    @PutMapping(value = "{id}/updateCategory")
    public ResponseEntity<Void> updateOfferingsCategory(@PathVariable("id") Long id,
                                                     @RequestParam Long categoryId,
                                                        @RequestParam Long updateCategoryId){
        offeringService.updateOfferingsCategory(id, categoryId, updateCategoryId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/")
    public ResponseEntity<Page<OfferingPreviewResponseDTO>> getOfferings(
            @ModelAttribute OfferingFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
            ){
        return ResponseEntity.ok(offeringService.getOfferings(filter, page, size, sortBy, sortDirection));
    }


    @GetMapping("/top")
    public ResponseEntity<Page<OfferingPreviewResponseDTO>> getTopOfferings(
    ){
        return ResponseEntity.ok(offeringService.getTopOfferings());
    }
}
