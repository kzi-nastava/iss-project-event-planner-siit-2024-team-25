package com.team25.event.planner.infrastructure;

import com.team25.event.planner.email.dto.ActivationEmailBodyDTO;
import com.team25.event.planner.email.dto.TestEmailBodyDTO;
import com.team25.event.planner.email.service.TemplateProcessorService;
import com.team25.event.planner.event.dto.EventInvitationEmailDTO;
import com.team25.event.planner.event.dto.EventInvitationShortEmailDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;

@Service
public class ThymeleafTemplateProcessorService implements TemplateProcessorService {
    private final String resourceUrl;

    private final SpringTemplateEngine templateEngine;

    public ThymeleafTemplateProcessorService(
            @Value("${email.static.resource-url}") String resourceUrl,
            SpringTemplateEngine templateEngine
    ) {
        this.resourceUrl = resourceUrl;
        this.templateEngine = templateEngine;
    }

    private Context getEmailTemplateContext() {
        Context context = new Context();
        context.setVariable("resourceUrl", resourceUrl);
        context.setVariable("currentYear", LocalDate.now().getYear());
        return context;
    }

    @Override
    public String getTestEmailBody(TestEmailBodyDTO dto) {
        Context context = getEmailTemplateContext();
        context.setVariable("name", dto.getRecipientName());
        context.setVariable("loginUrl", dto.getLoginUrl());

        return templateEngine.process("test", context);
    }

    @Override
    public String getAccountActivationEmailBody(ActivationEmailBodyDTO dto) {
        Context context = getEmailTemplateContext();
        context.setVariable("name", dto.getName());
        context.setVariable("activationUrl", dto.getActivationUrl());

        return templateEngine.process("activate", context);
    }

    @Override
    public String getEventInvitationEmailBody(String url, EventInvitationEmailDTO dto) {
        Context context = getEmailTemplateContext();
        context.setVariable("guestFirstName", dto.getGuestFirstName());
        context.setVariable("guestLastName", dto.getGuestLastName());
        context.setVariable("eventName", dto.getEventName());
        context.setVariable("eventDescription", dto.getEventDescription());
        context.setVariable("eventDate", dto.getEventDate().toString());
        context.setVariable("eventTime", dto.getEventTime().toString());
        context.setVariable("eventLink", url);
        context.setVariable("eventCountry", dto.getEventCountry());
        context.setVariable("eventCity", dto.getEventCity());
        context.setVariable("eventAddress", dto.getEventAddress());
        return templateEngine.process("event-invitation", context);
    }

    @Override
    public String getEventInvitationShortEmailBody(String url, EventInvitationShortEmailDTO dto) {
        Context context = getEmailTemplateContext();
        context.setVariable("eventName", dto.getEventName());
        context.setVariable("eventDescription", dto.getEventDescription());
        context.setVariable("eventDate", dto.getEventDate().toString());
        context.setVariable("eventTime", dto.getEventTime().toString());
        context.setVariable("eventLink", url);
        context.setVariable("eventCountry", dto.getEventCountry());
        context.setVariable("eventCity", dto.getEventCity());
        context.setVariable("eventAddress", dto.getEventAddress());
        return templateEngine.process("event-invitation", context);
    }

    @Override
    public String getServicePurchaseConfirmationBody(String url, Purchase purchase) {
        Context context = getEmailTemplateContext();
        context.setVariable("organizerName", purchase.getEvent().getOrganizer().getFullName());
        context.setVariable("serviceName", purchase.getOffering().getName());
        context.setVariable("eventName", purchase.getEvent().getName());
        context.setVariable("eventStartDate", purchase.getEvent().getStartDate());
        context.setVariable("eventStartTime", purchase.getEvent().getStartTime());
        context.setVariable("eventEndDate", purchase.getEvent().getEndDate());
        context.setVariable("eventEndTime", purchase.getEvent().getEndTime());
        context.setVariable("serviceStartDate", purchase.getStartDate());
        context.setVariable("serviceStartTime", purchase.getStartTime());
        context.setVariable("serviceEndDate", purchase.getEndDate());
        context.setVariable("serviceEndTime", purchase.getEndTime());
        context.setVariable("eventLocation", purchase.getEvent().getLocation().getCountry().toString() + ", " + purchase.getEvent().getLocation().getCity() + ", " +  purchase.getEvent().getLocation().getAddress());
        context.setVariable("serviceDetailsLink", url);
        context.setVariable("currentYear", LocalDate.now().getYear());
        return templateEngine.process("service-purchase-confirmation", context);
    }


}
