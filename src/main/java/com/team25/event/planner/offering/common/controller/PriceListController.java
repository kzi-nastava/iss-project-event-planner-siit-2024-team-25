package com.team25.event.planner.offering.common.controller;

import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemUpdateRequestDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemUpdateResponseDTO;
import com.team25.event.planner.offering.service.dto.ServiceResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("api/offerings/pricelist")
public class PriceListController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PriceListItemResponseDTO>> getPriceList(){
        Collection<PriceListItemResponseDTO> pricelist = setMockData();

        return new ResponseEntity<Collection<PriceListItemResponseDTO>>(pricelist, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListItemResponseDTO> getPriceListItem(@PathVariable Long id){
        PriceListItemResponseDTO item = new PriceListItemResponseDTO();
        item.setId(1L);
        if(!Objects.equals(item.getId(), id)){
            return new ResponseEntity<PriceListItemResponseDTO>(HttpStatus.NOT_FOUND);
        }
        item.setNumber(1);
        item.setName("Photography Package");
        item.setPrice(500.0);
        item.setDiscount(10.0);
        item.setPriceWithDiscount(item.getPrice() - item.getPrice()*item.getDiscount()/100);
        return new ResponseEntity<PriceListItemResponseDTO>(item, HttpStatus.OK);
    }

    private Collection<PriceListItemResponseDTO> setMockData(){
        Collection<PriceListItemResponseDTO> pricelist = new ArrayList<>();
        PriceListItemResponseDTO item1 = new PriceListItemResponseDTO();
        item1.setId(1L);
        item1.setNumber(1);
        item1.setName("Photography Package");
        item1.setPrice(500.0);
        item1.setDiscount(10.0);
        item1.setPriceWithDiscount(item1.getPrice() - item1.getPrice()*item1.getDiscount()/100);

        PriceListItemResponseDTO item2 = new PriceListItemResponseDTO();
        item2.setId(2L);
        item2.setNumber(2);
        item2.setName("Video Package");
        item2.setPrice(1000.0);
        item2.setDiscount(15.0);
        item2.setPriceWithDiscount(item2.getPrice() - item2.getPrice()*item2.getDiscount()/100);

        PriceListItemResponseDTO item3 = new PriceListItemResponseDTO();
        item3.setId(3L);
        item3.setNumber(3);
        item3.setName("Drone Package");
        item3.setPrice(800.0);
        item3.setDiscount(5.0);
        item3.setPriceWithDiscount(item3.getPrice() - item3.getPrice()* item3.getDiscount()/100);
        pricelist.add(item1);
        pricelist.add(item2);
        pricelist.add(item3);
        return pricelist;

    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListItemUpdateResponseDTO> updatePriceListItem(@PathVariable Long id, @RequestBody PriceListItemUpdateRequestDTO updateItem){
        PriceListItemUpdateResponseDTO item = new PriceListItemUpdateResponseDTO();
        item.setId(1L);
        if(!Objects.equals(item.getId(), id)){
            return new ResponseEntity<PriceListItemUpdateResponseDTO>(HttpStatus.NOT_FOUND);
        }
        item.setPrice(updateItem.getPrice());
        item.setDiscount(updateItem.getDiscount());
        item.setPriceWithDiscount(item.getPrice() - item.getPrice()*item.getDiscount()/100);

        return new ResponseEntity<PriceListItemUpdateResponseDTO>(item, HttpStatus.OK);
    }
}
