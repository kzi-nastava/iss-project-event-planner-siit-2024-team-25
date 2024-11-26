package com.team25.event.planner.user.service;

import com.team25.event.planner.user.dto.ReportRequestDTO;
import com.team25.event.planner.user.dto.ReportResponseDTO;
import com.team25.event.planner.user.mapper.ReportMapper;
import com.team25.event.planner.user.model.Report;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.ReportRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReportService {
    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportResponseDTO createReport(@Valid ReportRequestDTO requestDTO, Long reportedUserId) {
//        User user = userService.Get(requestDTO.getUserId());
//        User reportedUser = userService.Get(reportedUserId);
          User user = new User();
          user.setId(requestDTO.getUserId());
          User reportedUser = new User();
          reportedUser.setId(reportedUserId);
          reportedUser.setFirstName("First name");
          reportedUser.setLastName("Last name");

        Report report = reportMapper.toReport(requestDTO, user, reportedUser);
//        report = reportRepository.save(report);
        return reportMapper.toDTO(report);
    }
}
