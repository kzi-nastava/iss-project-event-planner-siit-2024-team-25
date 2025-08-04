package com.team25.event.planner.common.mapper;

import com.team25.event.planner.common.dto.ReviewRequestDTO;
import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.model.Review;
import com.team25.event.planner.event.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReviewMapperHelper.class})
public interface ReviewMapper {

    @Mapping(target = "purchaseId", source = "purchase.id")
    ReviewResponseDTO toDTO(Review review);

    @Mapping(target = "user.id", source = "userId")
    Review reviewFromDTO(ReviewRequestDTO reviewDTO);



}
