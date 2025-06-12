package com.team25.event.planner.user.controller;

import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.offering.product.dto.ProductResponseDTO;
import com.team25.event.planner.offering.service.dto.ServiceCardResponseDTO;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.service.UserFavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserFavoritesController {
    private final UserFavoritesService userFavoritesService;

    @GetMapping(value = "/{id}/favourite-services", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ServiceCardResponseDTO>> getFavouriteServices(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userFavoritesService.getFavoriteServices(id));
    }
    @GetMapping(value = "/{id}/favourite-products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ProductResponseDTO>> getFavouriteProducts(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userFavoritesService.getFavoriteProducts(id));
    }


    @PostMapping(value = "/{id}/favourite-services", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #id")
    public ResponseEntity<ServiceCardResponseDTO> addFavouriteService(@PathVariable("id") Long id, @RequestBody FavouriteOfferingDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFavoritesService.addFavoriteService(id, requestDTO));
    }

    @PostMapping(value = "/{id}/favourite-products", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #id")
    public ResponseEntity<ProductResponseDTO> addFavouriteProduct(@PathVariable("id") Long id, @RequestBody FavouriteOfferingDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFavoritesService.addFavoriteProduct(id, requestDTO));
    }
    @DeleteMapping("/{userId}/favourite-service/{favId}")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<?> removeServiceFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long favId) {
        userFavoritesService.removeServiceFromFavorites(userId, favId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/{userId}/favourite-product/{favId}")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<?> removeProductFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long favId) {
        userFavoritesService.removeProductFromFavorites(userId, favId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/favorite-events", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<Collection<EventPreviewResponseDTO>> getFavoriteEvents(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userFavoritesService.getFavoriteEvents(userId));
    }

    @PostMapping(value = "/{id}/favorite-events", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<EventPreviewResponseDTO> addFavoriteEvent(
            @PathVariable("id") Long userId,
            @RequestBody FavoriteEventRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFavoritesService.addFavoriteEvent(userId, requestDTO));
    }

    @DeleteMapping("/{userId}/favorite-events/{favId}")
    @PreAuthorize("hasRole('ROLE_USER') and authentication.principal.userId == #userId")
    public ResponseEntity<Void> removeEventFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long favId) {
        userFavoritesService.removeEventFromFavorites(userId, favId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
