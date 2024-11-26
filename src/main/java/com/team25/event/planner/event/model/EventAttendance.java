package com.team25.event.planner.event.model;

import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EventAttendance {
    @EmbeddedId
    private EventAttendanceId id;

    @NotNull
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "attendeeId", nullable = false)
    private User attendee;

    @NotNull
    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;
}
