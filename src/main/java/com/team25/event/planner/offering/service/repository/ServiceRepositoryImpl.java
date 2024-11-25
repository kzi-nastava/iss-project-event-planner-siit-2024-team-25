package com.team25.event.planner.offering.service.repository;

import com.team25.event.planner.offering.service.dto.*;

import java.util.Collection;
import java.util.List;

public class ServiceRepositoryImpl {

    public Collection<ServiceResponseDTO> getAll() {
        return List.of();
    }

    public ServiceResponseDTO getOne(Long id) {
        return null;
    }

    public Collection<ServiceResponseDTO> search(String from) {
        return List.of();
    }

    public ServiceCreateResponseDTO create(ServiceCreateRequestDTO createService) {
        return null;
    }

    public ServiceUpdateResponseDTO update(ServiceUpdateRequestDTO updateService) {
        return null;
    }

    public void delete(Long id) {

    }
}
