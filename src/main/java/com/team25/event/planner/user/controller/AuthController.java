package com.team25.event.planner.user.controller;

import com.team25.event.planner.common.dto.ErrorResponseDTO;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.exception.UnauthenticatedError;
import com.team25.event.planner.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @ModelAttribute RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequestDTO));
    }

    @PostMapping("/register/quick")
    public ResponseEntity<QuickRegisterResponseDTO> quickRegister(@Valid @ModelAttribute QuickRegisterRequestDTO quickRegisterRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.quickRegister(quickRegisterRequestDTO));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@Valid @RequestBody VerificationCodeDTO dto) {
        authService.activateAccount(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PutMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<Void> deactivateAccount() {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UnauthenticatedError.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthenticatedError(UnauthenticatedError error) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), error.getMessage())
        );
    }
}
