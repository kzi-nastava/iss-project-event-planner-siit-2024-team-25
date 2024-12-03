package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.RegistrationRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    Optional<RegistrationRequest> findByVerificationCode(@NotNull String verificationCode);

    @Query("select r.user.id from RegistrationRequest r where r.expirationTime < current_timestamp")
    Collection<Long> findExpiredUserIds();

    @Modifying
    @Query("delete from RegistrationRequest where expirationTime < current_timestamp")
    void deleteAllExpired();
}