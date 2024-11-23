package com.team25.event.planner.offering.service.repository;

import com.team25.event.planner.offering.service.dto.*;

import java.util.Collection;
import java.util.List;

public class ServiceRepositoryImpl implements ServiceRepository {

    @Override
    public Collection<ServiceResponseDTO> getAll() {
        return List.of();
    }

    @Override
    public ServiceResponseDTO getOne(Long id) {
        return null;
    }

    @Override
    public Collection<ServiceResponseDTO> search(String from) {
        return List.of();
    }

    @Override
    public ServiceCreateResponseDTO create(ServiceCreateRequestDTO createService) {
        return null;
    }

    @Override
    public ServiceUpdateResponseDTO update(ServiceUpdateRequestDTO updateService) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
