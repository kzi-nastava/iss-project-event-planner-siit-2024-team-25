package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.dto.TestEmailBodyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailGeneratorService {
    private final TemplateProcessorService templateProcessorService;

    public EmailDTO getTestEmail(String recipientEmail, String recipientName) {
        // Here goes the logic of converting domain data (recipientEmail, recipientName)
        // into a dto with the information that the template service needs.
        TestEmailBodyDTO bodyDto = new TestEmailBodyDTO(recipientName, "/user/login");

        String body = templateProcessorService.getTestEmailBody(bodyDto);

        return new EmailDTO(recipientEmail, "Test Email", body);
    }
}
