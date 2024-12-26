package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.PurchaseProductRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceRequestDTO;
import com.team25.event.planner.event.dto.PurchaseServiceResponseDTO;
import com.team25.event.planner.event.dto.PurchasedProductResponseDTO;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.event.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/purchase/")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;


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
}
