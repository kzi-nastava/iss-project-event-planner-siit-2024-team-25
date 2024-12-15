package com.team25.event.planner.user.controller;

import com.team25.event.planner.user.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("api/users")
public class UserFavoritesController {

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
    @Secured("ROLE_USER")
    public ResponseEntity<Collection<FavoriteEventResponseDTO>> getFavoriteEvents(@PathVariable("id") Long id) {
        Collection<FavoriteEventResponseDTO> responseDTOS = new ArrayList<>();

        FavoriteEventResponseDTO event1 = new FavoriteEventResponseDTO();
        event1.setId(1L);
        event1.setName("Summer Music Festival");
        event1.setDescription("Annual outdoor music festival with top artists");
        event1.setEventType("Music");

        FavoriteEventResponseDTO event2 = new FavoriteEventResponseDTO();
        event2.setId(2L);
        event2.setName("Tech Innovation Conference");
        event2.setDescription("Leading technology and innovation conference");
        event2.setEventType("Conference");

        responseDTOS.add(event1);
        responseDTOS.add(event2);

        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/favorite-events", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_USER")
    public ResponseEntity<FavoriteEventResponseDTO> addFavoriteEvent(
            @PathVariable("id") Long id,
            @RequestBody FavoriteEventRequestDTO requestDTO) {

        // Validate user ID
        if (!Objects.equals(id, requestDTO.getUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Create response DTO with event details
        FavoriteEventResponseDTO responseDTO = new FavoriteEventResponseDTO();
        responseDTO.setId(requestDTO.getEventId());
        responseDTO.setName("Summer Music Festival"); // Mock data
        responseDTO.setDescription("Annual outdoor music festival with top artists");
        responseDTO.setEventType("Music");

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/favorite-events/{favId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> removeEventFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long favId) {

        // Mock check to simulate finding the favorite event
        FavoriteEventResponseDTO mockEvent = new FavoriteEventResponseDTO();
        mockEvent.setId(1L);

        if (!Objects.equals(mockEvent.getId(), favId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
