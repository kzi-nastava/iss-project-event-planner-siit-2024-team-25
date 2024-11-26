package com.team25.event.planner.user.mapper;

import com.team25.event.planner.user.dto.ReportRequestDTO;
import com.team25.event.planner.user.dto.ReportResponseDTO;
import com.team25.event.planner.user.model.Report;
import com.team25.event.planner.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(target = "reportedUserFirstName", source = "report.reportedUser.firstName")
    @Mapping(target = "reportedUserLastName", source = "report.reportedUser.lastName")
    @Mapping(target = "userId", source = "report.user.id")
    @Mapping(target = "reportedUserId", source = "report.reportedUser.id")
    ReportResponseDTO toDTO(Report report);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportedUser", source = "reportedUser")
    @Mapping(target = "reportMessage", source = "userReportRequestDTO.reportMessage")
    Report toReport(ReportRequestDTO userReportRequestDTO, User user, User reportedUser);
}
