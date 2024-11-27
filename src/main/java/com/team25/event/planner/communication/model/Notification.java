package com.team25.event.planner.communication.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String message;

    @NotNull
    @Column(nullable = false)
    private Boolean isViewed;

    @NotNull
    @Column(nullable = false)
    private Long entityId;

    @NotNull
    @Column(nullable = false)

    @NotNull(message = "Privacy type is required")
    @Enumerated(EnumType.STRING)
    private NotificationCategory notificationCategory;

    @CreatedDate
    private Instant createdDate;

}
