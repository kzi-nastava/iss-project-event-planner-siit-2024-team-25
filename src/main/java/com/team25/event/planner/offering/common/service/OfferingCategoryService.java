package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.event.repository.BudgetItemRepository;
import com.team25.event.planner.offering.common.dto.*;
import com.team25.event.planner.offering.common.mapper.OfferingCategoryCommonMapper;
import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.model.OfferingCategory;
import com.team25.event.planner.offering.common.model.OfferingCategoryType;
import com.team25.event.planner.offering.common.model.OfferingType;
import com.team25.event.planner.offering.common.repository.OfferingCategoryRepository;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OfferingCategoryService {

    private final OfferingCategoryRepository offeringCategoryRepository;
    private final OfferingRepository offeringRepository;
    private final OfferingCategoryCommonMapper offeringCategoryCommonMapper;
    private final BudgetItemRepository budgetItemRepository;
    private final NotificationService notificationService;

    public List<OfferingCategoryResponseDTO> getOfferingCategories() {
        return offeringCategoryRepository.findOfferingCategoriesByStatus(OfferingCategoryType.ACCEPTED).stream().map(offeringCategoryCommonMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<OfferingCategoryPreviewResponseDTO> getAllOfferingCategories() {
        return offeringCategoryRepository.findOfferingCategoriesByStatus(OfferingCategoryType.ACCEPTED).stream().map(offeringCategoryCommonMapper::toPreviewResponseDTO).collect(Collectors.toList());
    }

    public List<OfferingCategoryResponseDTO> getSubmittedOfferingCategories() {
        return offeringCategoryRepository.findOfferingCategoriesByStatus(OfferingCategoryType.PENDING).stream().map(offeringCategoryCommonMapper::toResponseDTO).collect(Collectors.toList());
    }

    public OfferingCategoryResponseDTO getOfferingCategoryById(Long id) {
        return offeringCategoryRepository.findById(id).map(offeringCategoryCommonMapper::toResponseDTO).orElseThrow(()-> new NotFoundError("Offering category not found"));
    }

    public OfferingCategoryResponseDTO getOfferingCategory(Long id) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findOfferingCategoryByIdAndStatus(id, "ACCEPTED");

        if(offeringCategory == null) {
            throw new NotFoundError("Offering category not found");
        }
        return offeringCategoryCommonMapper.toResponseDTO(offeringCategory);
    }

    public OfferingCategoryResponseDTO getOfferingCategorySubmitted(Long id) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findOfferingCategoryByIdAndStatus(id, "PENDING");
        if(offeringCategory == null) {
            throw new NotFoundError("Submitted offering category not found");
        }
        return offeringCategoryCommonMapper.toResponseDTO(offeringCategory);
    }

    public OfferingCategoryResponseDTO createOfferingCategory(OfferingCategoryCreateRequestDTO offeringCategoryRequestDTO, OfferingCategoryType status) {
        OfferingCategory offeringCategory = offeringCategoryCommonMapper.toOfferingCategory(offeringCategoryRequestDTO);
        offeringCategory.setStatus(status);
        notificationService.sendOfferingCategoryNotificationToAdmin(offeringCategory);
        return offeringCategoryCommonMapper.toResponseDTO(offeringCategoryRepository.save(offeringCategory));
    }

    public OfferingCategoryResponseDTO updateOfferingCategory(Long id, OfferingCategoryUpdateRequestDTO offeringCategoryUpdateRequestDTO) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id).orElseThrow(() -> new NotFoundError("Offering category not found"));

        offeringCategory.setName(offeringCategoryUpdateRequestDTO.getName());
        offeringCategory.setDescription(offeringCategoryUpdateRequestDTO.getDescription());
        offeringCategory.setStatus(offeringCategoryUpdateRequestDTO.getStatus());

        notificationService.sendOfferingCategoryUpdateNotificationToOwner(offeringCategory);

        return offeringCategoryCommonMapper.toResponseDTO(offeringCategoryRepository.save(offeringCategory));
    }

    @Transactional
    public OfferingCategoryResponseDTO approveOfferingCategory(Long id, OfferingCategoryUpdateRequestDTO requestDTO, Long offeringId){
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id).orElseThrow(() -> new NotFoundError("Offering category not found"));
        Offering offering = offeringRepository.findById(offeringId).orElseThrow(() -> new NotFoundError("Offering not found"));

        offeringCategory.setName(requestDTO.getName());
        offeringCategory.setDescription(requestDTO.getDescription());
        offeringCategory.setStatus(OfferingCategoryType.ACCEPTED);

        offering.setStatus(OfferingType.ACCEPTED);
        offeringRepository.save(offering);
        notificationService.sendOfferingCategoryApproveNotificationToAdmin(offeringCategory, offering.getOwner());
        return offeringCategoryCommonMapper.toResponseDTO(offeringCategoryRepository.save(offeringCategory));
    }

    @Transactional
    public ResponseEntity<?> deleteOfferingCategory(Long id) {
        if(offeringCategoryRepository.existsById(id)) {
            if(offeringCategoryRepository.existsInUnlinkedOfferingType(id)){
                deleteOfferingFromEventTypes(id);
                deleteBudgetItemsByOfferingId(id);
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

    private void deleteBudgetItemsByOfferingId(Long offeringId) {
        budgetItemRepository.deleteAllByOfferingCategory(offeringId);
    }
    private  void deleteOfferingFromEventTypes(Long offeringId){
        offeringCategoryRepository.deleteCategoryFromEventTypes(offeringId);
    }
}
