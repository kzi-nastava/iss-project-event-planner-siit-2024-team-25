package com.team25.event.planner.offering.service.repository;

import com.team25.event.planner.offering.service.dto.*;

import java.util.Collection;

public interface ServiceRepository {

    Collection<ServiceResponseDTO> getAll();

    ServiceResponseDTO getOne(Long id);

    Collection<ServiceResponseDTO> search(String from);

    ServiceCreateResponseDTO create(ServiceCreateRequestDTO createService);

    ServiceUpdateResponseDTO update(ServiceUpdateRequestDTO updateService);

    void delete(Long id);
}
