package com.team25.event.planner.user.controller;

import com.team25.event.planner.user.dto.ReportFilterDTO;
import com.team25.event.planner.user.dto.ReportRequestDTO;
import com.team25.event.planner.user.dto.ReportResponseDTO;
import com.team25.event.planner.user.dto.ReportUpdateRequestDTO;
import com.team25.event.planner.user.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping(value = "/report")
    public ResponseEntity<Void> createReport(
            @RequestBody ReportRequestDTO requestDTO
    ) {
        reportService.createReport(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/reports")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<ReportResponseDTO>> getReports(
            @ModelAttribute ReportFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reportedUserId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(reportService.getReports(filter, page, size, sortBy, sortDirection));
    }

    @PutMapping(value = "/reports")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ReportResponseDTO> updateReport(
            @RequestBody ReportUpdateRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.updateReport(requestDTO));
    }
}
