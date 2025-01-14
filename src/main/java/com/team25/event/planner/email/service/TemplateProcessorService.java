package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.ActivationEmailBodyDTO;
import com.team25.event.planner.email.dto.TestEmailBodyDTO;
import com.team25.event.planner.event.dto.EventInvitationEmailDTO;
import com.team25.event.planner.event.dto.EventInvitationShortEmailDTO;
import com.team25.event.planner.event.model.Event;
import com.team25.event.planner.event.model.Purchase;
import com.team25.event.planner.offering.service.model.Service;

public interface TemplateProcessorService {
    String getTestEmailBody(TestEmailBodyDTO dto);

    String getAccountActivationEmailBody(ActivationEmailBodyDTO dto);

    String getEventInvitationEmailBody(String url, EventInvitationEmailDTO dto);

    String getEventInvitationShortEmailBody(String url, EventInvitationShortEmailDTO dto);

    String getServicePurchaseConfirmationBody(String url, Purchase purchase);
}
