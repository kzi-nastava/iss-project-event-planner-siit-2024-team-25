package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemUpdateRequestDTO;
import com.team25.event.planner.offering.common.service.PriceListService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/price-list/")
@AllArgsConstructor
public class PriceListController {
    private final PriceListService priceListService;

    @GetMapping(value = "{ownerId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PriceListItemResponseDTO>> getPriceList(@PathVariable("ownerId")Long ownerId){
        return ResponseEntity.ok(priceListService.getProductsPriceList(ownerId));
    }

    @GetMapping(value = "{ownerId}/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PriceListItemResponseDTO>> getServices(@PathVariable("ownerId")Long ownerId){
        return ResponseEntity.ok(priceListService.getServicesPriceList(ownerId));
    }

    @PutMapping(value = "{offeringId}")
    public ResponseEntity<PriceListItemResponseDTO> updatePrice(@PathVariable("offeringId") Long offeringId,
                                                                @RequestBody PriceListItemUpdateRequestDTO priceListItemUpdateRequestDTO){
        return ResponseEntity.ok(priceListService.updatePriceListItem(offeringId, priceListItemUpdateRequestDTO));
    }

    @GetMapping(value = "/{offeringId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListItemResponseDTO> getPriceListItem(@PathVariable Long offeringId){
        return ResponseEntity.ok(priceListService.getPriceListItem(offeringId));
    }

}
