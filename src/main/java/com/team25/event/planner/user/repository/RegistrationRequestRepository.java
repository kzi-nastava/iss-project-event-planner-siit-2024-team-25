package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.RegistrationRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    Optional<RegistrationRequest> findByVerificationCode(@NotNull String verificationCode);
}