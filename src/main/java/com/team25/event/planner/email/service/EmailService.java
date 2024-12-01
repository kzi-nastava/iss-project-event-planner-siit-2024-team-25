package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.exception.EmailSendFailedException;
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
    public void sendTestEmail(String recipientEmail, String recipientName) {
        EmailDTO email = emailGeneratorService.getTestEmail(recipientEmail, recipientName);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.error("Failed to send test email.");
        }
    }
}
