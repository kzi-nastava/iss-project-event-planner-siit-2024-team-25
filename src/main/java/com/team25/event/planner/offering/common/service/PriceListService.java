package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import com.team25.event.planner.offering.common.dto.PriceListItemUpdateRequestDTO;
import com.team25.event.planner.offering.common.mapper.PriceListMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.offering.product.repository.ProductRepository;
import com.team25.event.planner.offering.service.repository.ServiceRepository;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceListService {
    private final OfferingRepository offeringRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final OwnerRepository ownerRepository;
    private final PriceListMapper priceListMapper;


    public List<PriceListItemResponseDTO> getProductsPriceList(Long ownerId){
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(()->new NotFoundError("Owner is not found"));
        return productRepository.findAllByOwner(owner).stream().map(priceListMapper::toPriceListItem).collect(Collectors.toList());
    }

    public List<PriceListItemResponseDTO> getServicesPriceList(Long ownerId){
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(()->new NotFoundError("Owner is not found"));
        return serviceRepository.findAllByOwner(owner).stream().map(priceListMapper::toPriceListItem).collect(Collectors.toList());
    }

    public PriceListItemResponseDTO updatePriceListItem(Long offeringId, PriceListItemUpdateRequestDTO requestDTO){
        Offering offering = offeringRepository.findById(offeringId).orElseThrow(() -> new NotFoundError("Offering is not found"));
        offering.setPrice(requestDTO.getPrice());
        offering.setDiscount(requestDTO.getDiscount());
        return priceListMapper.toPriceListItem(offeringRepository.save(offering));
    }

    public PriceListItemResponseDTO getPriceListItem(Long offeringId){
        Offering offering = offeringRepository.findById(offeringId).orElseThrow(() -> new NotFoundError("Offering is not found"));
        return priceListMapper.toPriceListItem(offering);
    }
}
