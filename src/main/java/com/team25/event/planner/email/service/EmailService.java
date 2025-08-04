package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.exception.EmailSendFailedException;
import com.team25.event.planner.event.dto.EventInvitationEmailDTO;
import com.team25.event.planner.event.dto.EventInvitationRequestDTO;
import com.team25.event.planner.event.dto.EventInvitationShortEmailDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.RegistrationRequest;
import jakarta.validation.constraints.NotNull;
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

    @Async
    public void sendEventInvitationEmail(String guestEmail, EventInvitationEmailDTO eventInvitationEmailDTO) {
        EmailDTO email = emailGeneratorService.getEventInvitationEmail(guestEmail, eventInvitationEmailDTO);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.warn("Failed to send event invitation email for user: {} for event: {}",guestEmail, eventInvitationEmailDTO.getEventName());
        }
    }

    @Async
    public void sendQuickRegisterEmail(String guestEmail, EventInvitationShortEmailDTO eventInvitationShortEmailDTO) {
        EmailDTO email = emailGeneratorService.getQuickRegisterEmail(guestEmail, eventInvitationShortEmailDTO);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.warn("Failed to send event invitation email for unregistered user for event: {}",guestEmail);
        }
    }

    public void sendServicePurchaseConfirmation(Purchase purchase) {
        if(purchase == null){
            throw new IllegalArgumentException("Purchase is null");
        }
        EmailDTO email1 = emailGeneratorService.getServicePurchaseConfirmationEmail(purchase.getEvent().getOrganizer().getAccount().getEmail(),purchase);
        EmailDTO email2 = emailGeneratorService.getServicePurchaseConfirmationEmail(purchase.getOffering().getOwner().getAccount().getEmail(),purchase);
        try {
            emailSenderService.sendEmail(email1);
            emailSenderService.sendEmail(email2);
        } catch (EmailSendFailedException e) {
            logger.warn("Failed to send service purchase confirmation email.");
        }
    }
}
