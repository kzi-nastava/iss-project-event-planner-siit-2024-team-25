package com.team25.event.planner.common.service;

import com.team25.event.planner.common.dto.LatLongDTO;
import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.common.exception.ServerError;
import jakarta.validation.Valid;

public interface GeocodingService {
    LatLongDTO getLatLong(@Valid LocationRequestDTO location) throws ServerError;
}
