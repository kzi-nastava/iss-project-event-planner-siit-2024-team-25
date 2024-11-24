package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.OfferingPreviewResponseDTO;
import com.team25.event.planner.offering.common.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offerings")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;

    @GetMapping("/top")
    public ResponseEntity<Page<OfferingPreviewResponseDTO>> getTopOfferings(
            @RequestParam String country,
            @RequestParam String city
    ){
        return ResponseEntity.ok(offeringService.getTopOfferings(country, city));
    }
}
