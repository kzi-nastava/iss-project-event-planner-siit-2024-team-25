package com.team25.event.planner.event.controller;

import com.team25.event.planner.event.dto.PurchaseProductRequestDTO;
import com.team25.event.planner.event.dto.PurchasedProductResponseDTO;
import com.team25.event.planner.event.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/events")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping(value = "/{eventId}/products/{productsId}/purchase")
    public ResponseEntity<PurchasedProductResponseDTO> purchaseProduct(@PathVariable("eventId") Long eventId,
                                             @PathVariable("productsId") Long productId,
                                             @RequestBody PurchaseProductRequestDTO requestDTO) {

        try {
            PurchasedProductResponseDTO success = purchaseService.purchaseProduct(eventId, productId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(success);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
