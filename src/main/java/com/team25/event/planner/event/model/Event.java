package com.team25.event.planner.event.model;

import com.team25.event.planner.common.model.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private EventType eventType;

    @NotNull(message = "Name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    private Integer maxParticipants;

    @NotNull(message = "Privacy type is required")
    @Enumerated(EnumType.ORDINAL)
    private PrivacyType privacyType;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalTime endTime;

    @NotNull(message = "Location is required")
    @Column(nullable = false)
    private Location location;

    @CreatedDate
    private Instant createdDate;
}
