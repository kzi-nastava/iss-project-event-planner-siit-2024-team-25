package com.team25.event.planner.offering.service.mapper;


import com.team25.event.planner.offering.service.dto.ServiceCreateRequestDTO;
import com.team25.event.planner.offering.service.dto.ServiceCreateResponseDTO;
import com.team25.event.planner.offering.service.model.Service;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceCreateResponseDTO toDTO(Service service);
    Service toEntity(ServiceCreateRequestDTO dto);

}
