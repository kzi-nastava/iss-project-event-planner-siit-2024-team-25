package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.user.model.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OwnerResponseDTO extends UserResponseDTO {
    private String companyName;
    private LocationResponseDTO companyAddress;
    private PhoneNumber contactPhone;
    private String description;
    private List<String> companyPictures;
}
