package com.team25.event.planner.communication.model;


import com.team25.event.planner.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.List;

@Builder
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
    private String title;

    @NotNull
    @Column(nullable = false)
    private String message;

    @NotNull
    @Column(nullable = false)
    private Boolean isViewed;

    @NotNull
    @Column(nullable = false)
    private Long entityId;

    @NotNull(message = "Notification category is required")
    @Enumerated(EnumType.ORDINAL)
    private NotificationCategory notificationCategory;

    @ManyToOne
    private User user;

    @CreationTimestamp
    private Instant createdDate;

}
