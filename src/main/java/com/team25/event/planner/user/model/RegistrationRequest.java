package com.team25.event.planner.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "register_requests")
public class RegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String verificationCode;

    @NotNull
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant expirationTime;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private Account account;
}
