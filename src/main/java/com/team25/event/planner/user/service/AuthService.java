package com.team25.event.planner.user.service;

import com.team25.event.planner.user.dto.LoginRequestDTO;
import com.team25.event.planner.user.dto.LoginResponseDTO;
import com.team25.event.planner.user.dto.RegisterRequestDTO;
import com.team25.event.planner.user.dto.RegisterResponseDTO;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.model.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    public RegisterResponseDTO register(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = userService.createUser(registerRequestDTO);
        return new RegisterResponseDTO(
                registerRequestDTO.getEmail(),
                user.getFullName(),
                registerRequestDTO.getUserRole()
        );
    }

    public LoginResponseDTO login(@Valid LoginRequestDTO loginRequestDTO) {
        return new LoginResponseDTO(
                1L,
                loginRequestDTO.getEmail(),
                "Nikola Nikolic",
                UserRole.REGULAR,
                "token"
        );
    }
}
