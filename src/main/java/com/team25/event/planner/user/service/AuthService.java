package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.util.VerificationCodeGenerator;
import com.team25.event.planner.email.service.EmailService;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.service.EventService;
import com.team25.event.planner.security.jwt.JwtService;
import com.team25.event.planner.security.user.UserDetailsImpl;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.exception.UnauthenticatedError;
import com.team25.event.planner.user.mapper.UserMapper;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.RegistrationRequestRepository;
import com.team25.event.planner.user.repository.SuspensionRepository;
import com.team25.event.planner.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

@Service
public class AuthService {
    private static final int VERIFICATION_CODE_LENGTH = 64;
    private static final long REGISTRATION_REQUEST_CLEANUP_PERIOD_MILLIS = 1000 * 60 * 60 * 24; // 1 day

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EventService eventService;
    private final Duration activationTimeLimit;
    private final SuspensionRepository suspensionRepository;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AccountRepository accountRepository,
            RegistrationRequestRepository registrationRequestRepository,
            EmailService emailService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            UserMapper userMapper,
            EventService eventService,
            @Value("${activation.duration-minutes}") Long activationMinutesTimeLimit,
            SuspensionRepository suspensionRepository
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.eventService = eventService;
        this.activationTimeLimit = Duration.ofMinutes(activationMinutesTimeLimit);
        this.suspensionRepository = suspensionRepository;
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
    public QuickRegisterResponseDTO quickRegister(@Valid QuickRegisterRequestDTO quickRegisterRequestDTO){
        if (accountRepository.existsByEmail(quickRegisterRequestDTO.getEmail())) {
            throw new InvalidRequestError("Email address is already taken");
        }

        if(!eventService.checkInvitation(quickRegisterRequestDTO.getEmail(), quickRegisterRequestDTO.getInvitationCode())){
            throw new InvalidRequestError("There is no invitations for this email");
        }

        RegisterRequestDTO requestDTO  = userMapper.toRegisterRequestDto(quickRegisterRequestDTO);
        User user = userService.createUser(requestDTO);

        Account account = Account.builder()
                .email(quickRegisterRequestDTO.getEmail())
                .password(passwordEncoder.encode(quickRegisterRequestDTO
                .getPassword())).status(AccountStatus.ACTIVE)
                .user(user).build();
        accountRepository.save(account);
        user.setAccount(account);
        userRepository.save(user);

        eventService.createEventAttendance(user, quickRegisterRequestDTO.getInvitationCode());

        Event event = eventService.getEventByGuestAndInvitationCode(quickRegisterRequestDTO.getEmail(), quickRegisterRequestDTO.getInvitationCode());

        return new QuickRegisterResponseDTO(
                user.getId(),
                user.getAccount().getEmail(),
                quickRegisterRequestDTO.getPassword(),
                event.getId()
        );
    }

    @Transactional
    public void activateAccount(@Valid VerificationCodeDTO dto) {
        RegistrationRequest registrationRequest
                = registrationRequestRepository.findByVerificationCode(dto.getVerificationCode())
                .orElseThrow(() -> new NotFoundError("Activation code invalid or expired"));

        if (accountRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new InvalidRequestError("Email address is already taken");
        }

        if (registrationRequest.getExpirationTime().isBefore(Instant.now())) {
            registrationRequestRepository.delete(registrationRequest);
            throw new NotFoundError("Activation code invalid or expired");
        }

        Account account = Account.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .user(registrationRequest.getUser())
                .status(AccountStatus.ACTIVE)
                .build();

        registrationRequest.getUser().setAccount(account);

        accountRepository.save(account);
        registrationRequestRepository.delete(registrationRequest);
    }

    @Scheduled(fixedRate = REGISTRATION_REQUEST_CLEANUP_PERIOD_MILLIS)
    @Transactional
    public void cleanExpiredRequests() {
        Collection<Long> userIds = registrationRequestRepository.findExpiredUserIds();
        registrationRequestRepository.deleteAllExpired();
        if (!userIds.isEmpty()) {
            userRepository.deleteAllByIds(userIds);
        }
    }

    public LoginResponseDTO login(@Valid LoginRequestDTO loginRequestDTO) {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new UnauthenticatedError("Invalid credentials"));

            Suspension suspension = user.getAccount().getSuspension();
            if(suspension != null){
                Instant expirationTime = suspension.getExpirationTime();
                Instant now = Instant.now();

                if (expirationTime.isBefore(now)) {
                    user.getAccount().setSuspension(null);
                    suspensionRepository.deleteById(suspension.getId());
                }
                else{
                    return new LoginResponseDTO(userDetails.getUserId(),
                            userDetails.getUsername(),
                            user.getFullName(),
                            userDetails.getUserRole(),
                            null,
                            suspension.getExpirationTime()
                    );
                }
            }


            String jwt = jwtService.generateToken(userDetails);

            return new LoginResponseDTO(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    user.getFullName(),
                    userDetails.getUserRole(),
                    jwt,
                    null
            );
        } catch (DisabledException e) {
            throw new UnauthenticatedError("Account has been deactivated");
        } catch (BadCredentialsException e) {
            throw new UnauthenticatedError("Invalid credentials");
        } catch (AuthenticationException e) {
            throw new UnauthenticatedError(e.getMessage());
        }
    }

    public void resetPassword(long accountId, @Valid PasswordResetRequestDTO passwordResetRequest) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundError("Account not found"));

        if(!passwordEncoder.matches(passwordResetRequest.getOldPassword(), account.getPassword())) {
            throw new InvalidRequestError("Incorrect old password");
        }

        account.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        accountRepository.save(account);
    }
}
