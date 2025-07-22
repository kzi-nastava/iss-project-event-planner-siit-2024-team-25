package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.ActivationEmailBodyDTO;
import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.dto.TestEmailBodyDTO;
import com.team25.event.planner.event.dto.EventInvitationEmailDTO;
import com.team25.event.planner.event.dto.EventInvitationShortEmailDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.user.model.EventOrganizer;
import com.team25.event.planner.user.model.Owner;
import com.team25.event.planner.user.model.RegistrationRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailGeneratorService {
    private final TemplateProcessorService templateProcessorService;

    private final String frontendUrl;

    public EmailGeneratorService(
            TemplateProcessorService templateProcessorService,
            @Value("${frontend-url}") String frontendUrl
    ) {
        this.templateProcessorService = templateProcessorService;
        this.frontendUrl = frontendUrl;
    }

    public EmailDTO getTestEmail(String recipientEmail, String recipientName) {
        // Here goes the logic of converting domain data (recipientEmail, recipientName)
        // into a dto with the information that the template service needs.
        TestEmailBodyDTO bodyDto = new TestEmailBodyDTO(recipientName, frontendUrl + "/user/login");

        String body = templateProcessorService.getTestEmailBody(bodyDto);

        return new EmailDTO(recipientEmail, "Test Email", body);
    }

    public EmailDTO getAccountActivationEmail(RegistrationRequest registrationRequest) {
        final String email = registrationRequest.getEmail();

        final String activationUrl = frontendUrl + "/user/activate?code=" + registrationRequest.getVerificationCode();
        final ActivationEmailBodyDTO bodyDTO = new ActivationEmailBodyDTO(
                registrationRequest.getUser().getFullName(),
                activationUrl
        );
        final String body = templateProcessorService.getAccountActivationEmailBody(bodyDTO);

        return new EmailDTO(email, "Activate your Account at Event Planner", body);
    }

    public EmailDTO getEventInvitationEmail(String guestEmail, EventInvitationEmailDTO eventInvitationEmailDTO) {
        final String email = guestEmail;
        final String url = frontendUrl + "/event/" +eventInvitationEmailDTO.getEventId()+ "?invitationCode=" + eventInvitationEmailDTO.getEventInvitationCode();
        final String body = templateProcessorService.getEventInvitationEmailBody(url, eventInvitationEmailDTO);
        return new EmailDTO(email, "Event invitation", body);
    }


    public EmailDTO getQuickRegisterEmail(String guestEmail, EventInvitationShortEmailDTO eventInvitationShortEmailDTO) {
        final String email = guestEmail;
        final String url = frontendUrl + "/user/register/quick?invitationCode=" + eventInvitationShortEmailDTO.getEventInvitationCode();
        final String body = templateProcessorService.getEventInvitationShortEmailBody(url, eventInvitationShortEmailDTO);
        return new EmailDTO(email, "Event invitation", body);
    }

    public EmailDTO getServicePurchaseConfirmationEmail(String recipient, Purchase purchase){
        final String email = recipient;
        final String url = frontendUrl + "/service/services/" + purchase.getOffering().getId();
        final String body = templateProcessorService.getServicePurchaseConfirmationBody(url, purchase);
        return new EmailDTO(email, "Service purchase confirmation", body);
    }
}
