package com.team25.event.planner.event.service;

import com.team25.event.planner.common.dto.ReviewStatsResponseDTO;
import com.team25.event.planner.common.exception.ReportGenerationFailedException;
import com.team25.event.planner.event.dto.ActivityResponseDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface EventReportService {
    Resource generateEventDetailsReport(EventResponseDTO event, List<ActivityResponseDTO> agenda) throws ReportGenerationFailedException;

    Resource generateEventStatsReport(EventResponseDTO event, ReviewStatsResponseDTO reviewStats, int numAttendees);
}
