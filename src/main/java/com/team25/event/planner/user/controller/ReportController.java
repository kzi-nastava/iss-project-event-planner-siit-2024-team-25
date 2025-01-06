package com.team25.event.planner.user.controller;

import com.team25.event.planner.user.dto.*;
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
    @Secured("ROLE_USER")
    public ResponseEntity<ReportResponseDTO> createReport(
            @RequestBody ReportRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(requestDTO));
    }

    @GetMapping(value = "/reports")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<ReportResponseDTO>> getReports(
            @ModelAttribute ReportFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(reportService.getReports(filter, page, size, sortBy, sortDirection));
    }

    @PutMapping(value = "/report")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ReportResponseDTO> updateReport(
            @RequestBody ReportUpdateRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.updateReport(requestDTO));
    }

    @PostMapping(value = "/suspend")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<SuspensionResponseDTO> suspendUser(
            @RequestBody SuspensionRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.suspendUser(requestDTO));
    }

    @GetMapping(value = "/suspended")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<SuspensionResponseDTO>> getSuspensions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expirationTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(reportService.getSuspensions(page, size, sortBy, sortDirection));
    }
}
