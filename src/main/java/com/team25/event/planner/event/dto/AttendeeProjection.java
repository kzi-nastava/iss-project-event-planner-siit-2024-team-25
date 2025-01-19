package com.team25.event.planner.event.dto;

public interface AttendeeProjection {
    Long getUserId();
    String getFirstName();
    String getLastName();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
