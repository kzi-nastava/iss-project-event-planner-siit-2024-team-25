package com.team25.event.planner.user.service;

import com.team25.event.planner.user.dto.RegisterRequestDTO;
import com.team25.event.planner.user.dto.RegisterResponseDTO;
import com.team25.event.planner.user.model.User;
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
}
