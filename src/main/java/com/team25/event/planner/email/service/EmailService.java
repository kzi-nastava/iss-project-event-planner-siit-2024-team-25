package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.exception.EmailSendFailedException;
import com.team25.event.planner.event.dto.EventInvitationEmailDTO;
import com.team25.event.planner.event.dto.EventInvitationRequestDTO;
import com.team25.event.planner.user.model.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final EmailGeneratorService emailGeneratorService;
    private final EmailSenderService emailSenderService;

    @Async
    @SuppressWarnings("unused")
    public void sendTestEmail(String recipientEmail, String recipientName) {
        EmailDTO email = emailGeneratorService.getTestEmail(recipientEmail, recipientName);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.error("Failed to send test email.");
        }
    }

    @Async
    public void sendAccountActivationEmail(RegistrationRequest registrationRequest) {
        EmailDTO email = emailGeneratorService.getAccountActivationEmail(registrationRequest);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.warn("Failed to send account activation email for request: {}", registrationRequest.getId());
        }
    }

    public void sendEventInvitationEmail(String guestEmail, EventInvitationEmailDTO eventInvitationEmailDTO) {
        EmailDTO email = emailGeneratorService.getEventInvitationEmail(guestEmail, eventInvitationEmailDTO);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.warn("Failed to send event invitation email for user: {} for event: {}",guestEmail, eventInvitationEmailDTO.getEventName());
        }
    }
}
