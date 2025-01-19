package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.*;
import com.team25.event.planner.event.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/purchase/")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping(value = "events/{eventId}")
    public ResponseEntity<List<PurchasePreviewResponseDTO>> getPurchaseByEvent(@PathVariable("eventId")Long eventId) {
        return new ResponseEntity<>(purchaseService.getEventsPurchase(eventId), HttpStatus.OK);
    }
    @GetMapping(value = "offerings/{offeringId}")
    public ResponseEntity<List<PurchasePreviewResponseDTO>> getPurchaseByOffering(@PathVariable("offeringId")Long offeringId) {
        return new ResponseEntity<>(purchaseService.getOfferingsPurchase(offeringId), HttpStatus.OK);
    }

    @PostMapping(value = "events/{eventId}/products")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<PurchasedProductResponseDTO> purchaseProduct(@PathVariable("eventId") Long eventId,
                                             @RequestBody(required = false) PurchaseProductRequestDTO requestDTO) {

       return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.purchaseProduct(eventId, requestDTO));

    }

    @PostMapping(value = "event/{eventId}/service/{serviceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<PurchaseServiceResponseDTO> purchaseService(@PathVariable("eventId") Long eventId,
                                                      @PathVariable("serviceId") Long serviceId,
                                                      @RequestBody(required = false) PurchaseServiceRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.purchaseService(requestDTO, eventId, serviceId));
    }

    @GetMapping(value = "/service/{serviceId}/available")
    @Secured("ROLE_EVENT_ORGANIZER")
    public ResponseEntity<Boolean> isServiceAvailable(@PathVariable("serviceId") Long serviceId,
                                                      @ModelAttribute PurchaseServiceRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(purchaseService.isServiceAvailable(serviceId, requestDTO));
    }

    @GetMapping(value = "event/{eventId}/budget",produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("EVENT_ORGANIZER")
    public ResponseEntity<Double> getLeftMoneyFromBudgetItem(@PathVariable Long eventId, @RequestParam Long categoryId) {
        return ResponseEntity.ok(purchaseService.getLeftMoneyFromBudgetItem(eventId, categoryId));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseServicePreviewResponseDTO>> getOwnerPurchases(
            @RequestParam Long ownerId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(purchaseService.getOwnerPurchasesOverlappingDateRange(ownerId, startDate, endDate));
    }
}
