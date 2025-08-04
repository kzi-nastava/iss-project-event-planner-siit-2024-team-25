package com.team25.event.planner.event.mapper;

import com.team25.event.planner.event.dto.ActivityRequestDTO;
import com.team25.event.planner.event.dto.ActivityResponseDTO;
import com.team25.event.planner.event.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponseDTO toDTO(Activity activity);

    @Mapping(target = "id", ignore = true)
    Activity toActivity(ActivityRequestDTO activityRequestDTO);
}
