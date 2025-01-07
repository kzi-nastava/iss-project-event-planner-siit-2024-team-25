package com.team25.event.planner.user.mapper;

import com.team25.event.planner.user.dto.ReportRequestDTO;
import com.team25.event.planner.user.dto.ReportResponseDTO;
import com.team25.event.planner.user.model.Report;
import com.team25.event.planner.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(target = "reportedUserFirstName", source = "reportedUser.firstName")
    @Mapping(target = "reportedUserLastName", source = "reportedUser.lastName")
    @Mapping(target = "userFirstName", source = "user.firstName")
    @Mapping(target = "userLastName", source = "user.lastName")
    @Mapping(target = "reportedUserId", source = "reportedUser.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdDate", source = "createdDate")
    ReportResponseDTO toDTO(Report report);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportedUser", source = "reportedUser")
    @Mapping(target = "reportMessage", source = "userReportRequestDTO.reportMessage")
    Report toReport(ReportRequestDTO userReportRequestDTO, User user, User reportedUser);

    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
