package com.team25.event.planner.user.controller;

import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
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

    @GetMapping(value = "/{id}/favourite-offerings", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_USER")
    public ResponseEntity<Collection<FavouriteOfferingResponseDTO>> getFavouriteOfferings(@PathVariable("id") Long id) {
        Collection<FavouriteOfferingResponseDTO> responseDTOS = new ArrayList<>();

        FavouriteOfferingResponseDTO object1 = new FavouriteOfferingResponseDTO();
        object1.setId(1L);
        object1.setName("Basic Photography Package");
        object1.setDescription("A simple package including essential photography services.");
        object1.setPrice(300.00);

        FavouriteOfferingResponseDTO object2 = new FavouriteOfferingResponseDTO();
        object2.setId(2L);
        object2.setName("Premium Photography Package");
        object2.setDescription("An advanced package with additional features like drone photography.");
        object2.setPrice(800.00);

        responseDTOS.add(object1);
        responseDTOS.add(object2);

        return new ResponseEntity<Collection<FavouriteOfferingResponseDTO>>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/favourite-offerings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserOfferingFavResponseDTO> addFavouriteOffering(@PathVariable("id") Long id, @RequestBody UserOfferingFavRequestDTO requestDTO) {
        UserOfferingFavResponseDTO responseDTO = new UserOfferingFavResponseDTO();
        responseDTO.setId(id);
        if (!Objects.equals(id, requestDTO.getUserId())) {
            return new ResponseEntity<UserOfferingFavResponseDTO>(responseDTO, HttpStatus.FORBIDDEN);
        }

        responseDTO.setUserId(id);
        responseDTO.setOfferingId(requestDTO.getOfferingId());

        return new ResponseEntity<UserOfferingFavResponseDTO>(responseDTO, HttpStatus.CREATED);
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
