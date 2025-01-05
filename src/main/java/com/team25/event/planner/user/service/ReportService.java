package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.mapper.ReportMapper;
import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.Report;
import com.team25.event.planner.user.model.Suspension;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.ReportRepository;
import com.team25.event.planner.user.repository.SuspensionRepository;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.specification.ReportSpecification;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {
    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final ReportSpecification reportSpecification;
    private final UserRepository userRepository;
    private final SuspensionRepository suspensionRepository;

    public ReportResponseDTO createReport(@Valid ReportRequestDTO requestDTO) {
//        User user = userService.Get(requestDTO.getUserId());
//        User reportedUser = userService.Get(reportedUserId);
          User user = new User();
          user.setId(requestDTO.getUserId());
          User reportedUser = new User();
//          reportedUser.setId(reportedUserId);
          reportedUser.setFirstName("First name");
          reportedUser.setLastName("Last name");

        Report report = reportMapper.toReport(requestDTO, user, reportedUser);
//        report = reportRepository.save(report);
        return reportMapper.toDTO(report);
    }

    public Page<ReportResponseDTO> getReports(ReportFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
        Specification<Report> spec = reportSpecification.createSpecification(filter);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return reportRepository.findAll(spec, pageable).map(reportMapper::toDTO);
    }

    public ReportResponseDTO updateReport(@Valid ReportUpdateRequestDTO requestDTO) {
        Report report = reportRepository.findById(requestDTO.getReportId()).orElseThrow(() -> new InvalidRequestError("Report do not exist"));
        report.setIsViewed(requestDTO.getIsViewed());
        reportRepository.save(report);
        return reportMapper.toDTO(report);
    }

    public SuspensionResponseDTO suspendUser(SuspensionRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId()).orElseThrow(() -> new NotFoundError("User not found"));
        Report report = reportRepository.findById(requestDTO.getReportId()).orElseThrow(() -> new NotFoundError("Report not found"));
        report.setIsViewed(true);
        reportRepository.save(report);

        Instant expirationTime = Instant.now().plus(3, ChronoUnit.DAYS);
        Suspension suspension = Suspension.builder()
                .account(user.getAccount())
                .expirationTime(expirationTime)
                .build();
        suspensionRepository.save(suspension);
        SuspensionResponseDTO responseDTO = SuspensionResponseDTO.builder()
                .id(suspension.getId())
                .expirationTime(suspension.getExpirationTime())
                .userId(requestDTO.getUserId())
                .build();
        return responseDTO;
    }
}
