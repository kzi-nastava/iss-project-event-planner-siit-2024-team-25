package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.offering.common.dto.OfferingCategoryCreateRequestDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryResponseDTO;
import com.team25.event.planner.offering.common.dto.OfferingCategoryUpdateRequestDTO;
import com.team25.event.planner.offering.common.mapper.OfferingCategoryCommonMapper;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OfferingCategoryService {

    private final OfferingCategoryRepository offeringCategoryRepository;
    private final OfferingCategoryCommonMapper offeringCategoryCommonMapper;

    public List<OfferingCategoryResponseDTO> getOfferingCategories() {
        return offeringCategoryRepository.findAll().stream().map(offeringCategoryCommonMapper::toResponseDTO).collect(Collectors.toList());
    }

    public OfferingCategoryResponseDTO getOfferingCategory(Long id) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id).orElseThrow(() -> new NotFoundError("Offer category not found"));
        return offeringCategoryCommonMapper.toResponseDTO(offeringCategory);
    }

    public OfferingCategoryResponseDTO createOfferingCategory(OfferingCategoryCreateRequestDTO offeringCategoryRequestDTO) {
        OfferingCategory offeringCategory = offeringCategoryCommonMapper.toOfferingCategory(offeringCategoryRequestDTO);

        return offeringCategoryCommonMapper.toResponseDTO(offeringCategoryRepository.save(offeringCategory));
    }

    public OfferingCategoryResponseDTO updateOfferingCategory(Long id, OfferingCategoryUpdateRequestDTO offeringCategoryUpdateRequestDTO) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id).orElseThrow(() -> new NotFoundError("Offering category not found"));

        offeringCategory.setName(offeringCategoryUpdateRequestDTO.getName());
        offeringCategory.setStatus(offeringCategory.getStatus());

        return offeringCategoryCommonMapper.toResponseDTO(offeringCategoryRepository.save(offeringCategory));
    }

    public ResponseEntity<?> deleteOfferingCategory(Long id) {
        if(offeringCategoryRepository.existsById(id)) {
            if(offeringCategoryRepository.existsInUnlinkedOfferingType(id)){
                int rowsDeleted = offeringCategoryRepository.deleteOfferingTypeById(id);
                if(rowsDeleted == 1){
                    return ResponseEntity.noContent().build();
                }else{
                    throw new InvalidRequestError("Deletion of offering category failed");
                }
            }else{
                throw new InvalidRequestError("Some offering need this offering category, so you are not able to delete it");
            }
        }else{
            throw new NotFoundError("Offering category not found");
        }
    }
}
