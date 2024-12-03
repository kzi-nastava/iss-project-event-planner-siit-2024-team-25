package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.ActivationEmailBodyDTO;
import com.team25.event.planner.email.dto.TestEmailBodyDTO;

public interface TemplateProcessorService {
    String getTestEmailBody(TestEmailBodyDTO dto);

    String getAccountActivationEmailBody(ActivationEmailBodyDTO dto);
}
