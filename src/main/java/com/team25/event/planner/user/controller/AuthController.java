package com.team25.event.planner.user.controller;

import com.team25.event.planner.user.dto.PasswordResetRequestDTO;
import com.team25.event.planner.user.dto.RegisterRequestDTO;
import com.team25.event.planner.user.dto.RegisterResponseDTO;
import com.team25.event.planner.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequestDTO));
    }

    // TODO: login and refresh

    // password reset
    @PutMapping ("/password-reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return ResponseEntity.noContent().build();
    }

    // deactivate
    @DeleteMapping("/deactivate")
    public ResponseEntity<Void> deactivateAccount() {
        return ResponseEntity.noContent().build();
    }
}
