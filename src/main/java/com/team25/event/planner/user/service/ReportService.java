package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.event.dto.EventPreviewResponseDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.user.dto.ReportFilterDTO;
import com.team25.event.planner.user.dto.ReportRequestDTO;
import com.team25.event.planner.user.dto.ReportResponseDTO;
import com.team25.event.planner.user.dto.ReportUpdateRequestDTO;
import com.team25.event.planner.user.mapper.ReportMapper;
import com.team25.event.planner.user.model.Report;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.ReportRepository;
import com.team25.event.planner.user.repository.UserRepository;
import com.team25.event.planner.user.specification.ReportSpecification;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final CurrentUserService currentUserService;

    public ReportResponseDTO createReport(@Valid ReportRequestDTO requestDTO) {
        User user = userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(() -> new InvalidRequestError("User not found"));
        User reportedUser = userRepository.findById(requestDTO.getReportedUserId()).orElseThrow(() -> new InvalidRequestError("User not found"));

        Report report = reportMapper.toReport(requestDTO, user, reportedUser);
        report.setIsViewed(false);
        report = reportRepository.save(report);
        return reportMapper.toDTO(report);
    }

    public Page<ReportResponseDTO> getReports(ReportFilterDTO filter, int page, int size, String sortBy, String sortDirection) {
//        Specification<Report> spec = reportSpecification.createSpecification(filter);
//
//        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//
//        return reportRepository.findAll(spec, pageable).map(reportMapper::toDTO);

        User user = new User();
        user.setId(1L);
        User reportedUser = new User();
        reportedUser.setId(2L);
        reportedUser.setFirstName("First name");
        reportedUser.setLastName("Last name");
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        Report report = reportMapper.toReport(requestDTO, user, reportedUser);
        ReportResponseDTO responseDTO = reportMapper.toDTO(report);
        List<ReportResponseDTO> reportResponseDTO = Collections.singletonList(responseDTO);

        return new PageImpl<>(reportResponseDTO);
    }

    public ReportResponseDTO updateReport(@Valid ReportUpdateRequestDTO requestDTO) {
//        Report report = reportRepository.findById(requestDTO.getReportId()).orElseThrow(() -> new InvalidRequestError("Report do not exist"));
        Report report = new Report();
        report.setId(1L);
//        report.setIsViewed(false);
//        report.setIsViewed(requestDTO.getIsViewed());
//        reportRepository.save(report);

        return reportMapper.toDTO(report);
    }
}
