package com.team25.event.planner.common.dto;

import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Data
public class ResourceResponseDTO {
    private final Resource resource;
    private final String filename;
    private final MediaType mimeType;
}
