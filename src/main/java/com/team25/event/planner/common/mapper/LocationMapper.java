package com.team25.event.planner.common.mapper;

import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.common.model.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationResponseDTO toDTO(Location location);

    Location toLocation(LocationRequestDTO locationRequestDTO);
}
