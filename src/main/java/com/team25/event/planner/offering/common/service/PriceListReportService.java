package com.team25.event.planner.offering.common.service;

import com.team25.event.planner.common.exception.ReportGenerationFailedException;
import com.team25.event.planner.offering.common.dto.PriceListItemResponseDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface PriceListReportService {
    Resource generatePriceListReport(List<PriceListItemResponseDTO> priceListItemResponseDTOS) throws ReportGenerationFailedException;
}
