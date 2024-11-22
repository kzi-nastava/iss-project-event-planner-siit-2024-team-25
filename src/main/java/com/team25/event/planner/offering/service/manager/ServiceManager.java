package com.team25.event.planner.offering.service.manager;

import com.team25.event.planner.offering.service.dto.ServiceCreateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceCreateResponseDTO;
import com.team25.event.planner.offering.service.dto.ServiceUpdateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceUpdateResponseDTO;

import java.util.Collection;

public interface ServiceManager {
    Collection<ServiceCreateResponseDTO> findAll();

    Collection<ServiceCreateResponseDTO> searchServices(String from);

    ServiceCreateResponseDTO findOne(Long id);

    ServiceCreateResponseDTO create(ServiceCreateRequestDTO createService) throws Exception;

    ServiceUpdateResponseDTO update(ServiceUpdateRequestDTO updateService) throws  Exception;

    void delete(Long id);
}
