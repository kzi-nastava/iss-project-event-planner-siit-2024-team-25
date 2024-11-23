package com.team25.event.planner.user;

import com.team25.event.planner.user.dto.FavoriteOfferingResponseDTO;
import com.team25.event.planner.user.dto.UserOfferingFavRequestDTO;
import com.team25.event.planner.user.dto.UserOfferingFavResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("api/users")
public class UserController {

    @GetMapping(value = "/{id}/favorite-services", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<FavoriteOfferingResponseDTO>> getFavoriteServices(@PathVariable("id") Long id){
        Collection<FavoriteOfferingResponseDTO> responseDTOS = new ArrayList<>();

        FavoriteOfferingResponseDTO object1 = new FavoriteOfferingResponseDTO();
        object1.setId(1L);
        object1.setName("Basic Photography Package");
        object1.setDescription("A simple package including essential photography services.");
        object1.setPrice(300.00);

        FavoriteOfferingResponseDTO object2 = new FavoriteOfferingResponseDTO();
        object2.setId(2L);
        object2.setName("Premium Photography Package");
        object2.setDescription("An advanced package with additional features like drone photography.");
        object2.setPrice(800.00);

        responseDTOS.add(object1);
        responseDTOS.add(object2);

        return new ResponseEntity<Collection<FavoriteOfferingResponseDTO>>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/favorite-services", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserOfferingFavResponseDTO> addFavoriteService(@PathVariable("id") Long id, @RequestBody UserOfferingFavRequestDTO requestDTO) {
        UserOfferingFavResponseDTO responseDTO = new UserOfferingFavResponseDTO();
        responseDTO.setId(id);
        if(!Objects.equals(id, requestDTO.getUserId())){
            return new ResponseEntity<UserOfferingFavResponseDTO>(responseDTO, HttpStatus.FORBIDDEN);
        }

        responseDTO.setUserId(id);
        responseDTO.setOfferingId(requestDTO.getOfferingId());

        return new ResponseEntity<UserOfferingFavResponseDTO>(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}/favorite-services/{favId}")
    public ResponseEntity<?> deleteServiceFromFavorite(@PathVariable Long id,@PathVariable Long favId) {
        FavoriteOfferingResponseDTO service1 = new FavoriteOfferingResponseDTO();
        service1.setId(1L);
        if (!Objects.equals(service1.getId(), favId)) {
            return new ResponseEntity<FavoriteOfferingResponseDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
