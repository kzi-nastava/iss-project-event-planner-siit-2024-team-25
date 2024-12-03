package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.util.VerificationCodeGenerator;
import com.team25.event.planner.email.service.EmailService;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.RegistrationRequestRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {
    private static final int VERIFICATION_CODE_LENGTH = 64;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final EmailService emailService;

    private final Duration activationTimeLimit;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AccountRepository accountRepository,
            RegistrationRequestRepository registrationRequestRepository,
            EmailService emailService,
            @Value("${activation.duration-minutes}") Long activationMinutesTimeLimit
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.emailService = emailService;
        this.activationTimeLimit = Duration.ofMinutes(activationMinutesTimeLimit);
    }

    @Transactional
    public RegisterResponseDTO register(@Valid RegisterRequestDTO registerRequestDTO) {
        if (accountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new InvalidRequestError("Email address is already taken");
        }

        User user = userService.createUser(registerRequestDTO);

        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .user(user)
                .expirationTime(Instant.now().plus(activationTimeLimit))
                .verificationCode(VerificationCodeGenerator.generateVerificationCode(VERIFICATION_CODE_LENGTH))
                .build();

        emailService.sendAccountActivationEmail(registrationRequest);

        registrationRequestRepository.save(registrationRequest);

        return new RegisterResponseDTO(
                registrationRequest.getEmail(),
                user.getFullName(),
                user.getUserRole()
        );
    }

    @Transactional
    public void activateAccount(@Valid VerificationCodeDTO dto) {
        RegistrationRequest registrationRequest
                = registrationRequestRepository.findByVerificationCode(dto.getVerificationCode())
                .orElseThrow(NotFoundError::new);

        if (accountRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new InvalidRequestError("Email address is already taken");
        }

        if (registrationRequest.getExpirationTime().isBefore(Instant.now())) {
            registrationRequestRepository.delete(registrationRequest);
            throw new NotFoundError();
        }

        Account account = Account.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .user(registrationRequest.getUser())
                .status(AccountStatus.ACTIVE)
                .build();

        accountRepository.save(account);
        registrationRequestRepository.delete(registrationRequest);
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
