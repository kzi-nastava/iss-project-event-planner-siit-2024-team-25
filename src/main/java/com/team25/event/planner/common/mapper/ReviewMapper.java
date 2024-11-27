package com.team25.event.planner.common.mapper;

import com.team25.event.planner.common.dto.ReviewResponseDTO;
import com.team25.event.planner.common.model.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponseDTO toDTO(Review review);
}
