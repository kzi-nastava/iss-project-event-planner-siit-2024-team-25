package com.team25.event.planner.user.dto;

import com.team25.event.planner.common.dto.LocationRequestDTO;
import com.team25.event.planner.user.model.PhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Owner specific information
 */
@Data
public class OwnerRequestDTO {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotNull(message = "Company address is required")
    @Valid
    private LocationRequestDTO companyAddress;

    @NotNull(message = "Contact phone is required")
    @Valid
    private PhoneNumber contactPhone;

    @NotBlank(message = "Company description is required")
    private String description;

    private List<MultipartFile> companyPictures;

    private List<String> picturesToRemove;
}
