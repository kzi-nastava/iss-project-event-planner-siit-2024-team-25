package com.team25.event.planner.offering.product;

import com.team25.event.planner.offering.common.dto.OfferingCategoryServiceResponseDTO;
import com.team25.event.planner.offering.event.dto.EventTypeServiceResponseDTO;
import com.team25.event.planner.offering.product.dto.ProductDetailsResponseDTO;
import com.team25.event.planner.offering.service.dto.ServiceDetailsResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDetailsResponseDTO> getProduct(@PathVariable("id") Long id) {

        ProductDetailsResponseDTO productDetailsResponseDTO = new ProductDetailsResponseDTO();

        productDetailsResponseDTO.setId(1L);
        if (!Objects.equals(productDetailsResponseDTO.getId(), id)) { // if the service exist => update else not found the service
            return new ResponseEntity<ProductDetailsResponseDTO>(HttpStatus.NOT_FOUND);
        }


        productDetailsResponseDTO.setName("Wedding Photography");
        productDetailsResponseDTO.setDescription("Professional photography services for weddings.");
        productDetailsResponseDTO.setPrice(1000.00);
        productDetailsResponseDTO.setDiscount(10.0);
        productDetailsResponseDTO.setAvailable(false);

        ArrayList<String> images2 = new ArrayList<>();
        images2.add("wedding1.jpg");
        images2.add("wedding2.jpg");
        productDetailsResponseDTO.setImages(images2);

        ArrayList<EventTypeServiceResponseDTO> eventTypes2 = new ArrayList<>();
        EventTypeServiceResponseDTO eventType21 = new EventTypeServiceResponseDTO(3L, "Photography");
        EventTypeServiceResponseDTO eventType22 = new EventTypeServiceResponseDTO(4L, "Videography");
        eventTypes2.add(eventType21);
        eventTypes2.add(eventType22);
        productDetailsResponseDTO.setEventTypes(eventTypes2);

        productDetailsResponseDTO.setOfferingCategory(new OfferingCategoryServiceResponseDTO(1L, "Premium"));

        return new ResponseEntity<ProductDetailsResponseDTO>(productDetailsResponseDTO, HttpStatus.OK);
    }
}
