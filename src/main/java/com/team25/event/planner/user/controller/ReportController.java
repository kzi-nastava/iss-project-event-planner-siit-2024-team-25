package com.team25.event.planner.user.controller;

import com.team25.event.planner.user.dto.ReportRequestDTO;
import com.team25.event.planner.user.dto.ReportResponseDTO;
import com.team25.event.planner.user.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping(value = "/{reportedUserId}/report")
    public ResponseEntity<ReportResponseDTO> createReport(
            @PathVariable("reportedUserId") Long reportedUserId,
            @RequestBody ReportRequestDTO requestDTO
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(requestDTO, reportedUserId));
    }
}
