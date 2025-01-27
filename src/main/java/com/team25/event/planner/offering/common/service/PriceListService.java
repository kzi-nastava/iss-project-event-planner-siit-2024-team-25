package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.dto.ResourceResponseDTO;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.ReportGenerationFailedException;
import com.team25.event.planner.common.exception.ServerError;
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
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceListService {
    private final static String priceListReportFilenameTemplate = "price-list_$ID_$TIME.pdf";
    private final OfferingRepository offeringRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final OwnerRepository ownerRepository;
    private final PriceListMapper priceListMapper;
    private final PriceListReportService priceListReportService;


    public List<PriceListItemResponseDTO> getProductsPriceList(Long ownerId){
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(()->new NotFoundError("Owner is not found"));
        return productRepository.findAllByOwner(owner).stream().map(priceListMapper::toPriceListItem).collect(Collectors.toList());
    }

    public List<PriceListItemResponseDTO> getServicesPriceList(Long ownerId){
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(()->new NotFoundError("Owner is not found"));
        return serviceRepository.findAllByOwner(owner).stream().map(priceListMapper::toPriceListItem).collect(Collectors.toList());
    }

    public ResourceResponseDTO getPriceListReport(Long ownerId){
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(()->new NotFoundError("Owner is not found"));
        List<PriceListItemResponseDTO> priceListItemResponse = new ArrayList<>(getProductsPriceList(ownerId));
        priceListItemResponse.addAll(getServicesPriceList(ownerId));
        try {
            Resource resource = priceListReportService.generatePriceListReport(priceListItemResponse, owner.getFirstName() + " " + owner.getLastName());

            String filename = priceListReportFilenameTemplate
                    .replace("$ID", ownerId.toString())
                    .replace("$TIME", Long.toString(System.currentTimeMillis()));

            return new ResourceResponseDTO(resource, filename, MediaType.APPLICATION_PDF);

        } catch (ReportGenerationFailedException e) {
            throw new ServerError("Failed to generate price list report", 500);
        }
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
