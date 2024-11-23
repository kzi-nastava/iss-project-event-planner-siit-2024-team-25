package com.team25.event.planner.offering.service.manager;

import com.team25.event.planner.offering.service.dto.ServiceCreateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceCreateResponseDTO;
import com.team25.event.planner.offering.service.dto.ServiceUpdateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceUpdateResponseDTO;

import java.util.Collection;
import java.util.List;

public class ServiceManagerImpl implements ServiceManager {

    @Override
    public Collection<ServiceCreateResponseDTO> getAll() {
        return List.of();
    }

    @Override
    public Collection<ServiceCreateResponseDTO> searchServices(String from) {
        return List.of();
    }

    @Override
    public ServiceCreateResponseDTO getOne(Long id) {
        return null;
    }

    @Override
    public ServiceCreateResponseDTO create(ServiceCreateRequestDTO createService) throws Exception {
        return null;
    }

    @Override
    public ServiceUpdateResponseDTO update(ServiceUpdateRequestDTO updateService) throws Exception {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
