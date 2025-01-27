package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.common.dto.ResourceResponseDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemUpdateRequestDTO;
import com.team25.event.planner.offering.common.service.PriceListService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
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

    @GetMapping(value = "/{ownerId}/price-list-report")
    public ResponseEntity<Resource> getPriceListReport(@PathVariable("ownerId")Long ownerId,
    @RequestParam(value = "isProductList", defaultValue = "true") boolean isProductList){
        ResourceResponseDTO resourceResponse = priceListService.getPriceListReport(ownerId, isProductList);
        return ResponseEntity.ok()
                .contentType(resourceResponse.getMimeType())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(resourceResponse.getFilename())
                        .build().toString()
                )
                .body(resourceResponse.getResource());
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
