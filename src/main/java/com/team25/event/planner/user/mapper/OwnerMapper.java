package com.team25.event.planner.user.mapper;

import com.team25.event.planner.user.dto.OwnerResponseDTO;
import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "companyName", source = "ownerFields.companyName")
    @Mapping(target = "companyAddress", source = "ownerFields.companyAddress")
    @Mapping(target = "contactPhone", source = "ownerFields.contactPhone")
    @Mapping(target = "description", source = "ownerFields.description")
    @Mapping(target = "companyPictures", ignore = true)
    Owner toOwner(UserRequestDTO userRequestDTO);

    OwnerResponseDTO toDTO(Owner owner);
}
