package com.team25.event.planner.infrastructure;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.exception.EmailSendFailedException;
import com.team25.event.planner.email.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SendGridEmailSenderService implements EmailSenderService {
    private static final String EMAIL_ENDPOINT = "mail/send";

    private final SendGrid sendGrid;
    private final Email senderEmail;

    @Override
    public void sendEmail(EmailDTO email) throws EmailSendFailedException {
        Email recipientEmail = new Email(email.getRecipientEmail());
        Content content = new Content("text/html", email.getBody());
        Mail mail = new Mail(senderEmail, email.getSubject(), recipientEmail, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint(EMAIL_ENDPOINT);
            request.setBody(mail.build());

            sendGrid.api(request);
        } catch (IOException e) {
            throw new EmailSendFailedException(e.getMessage());
        }
    }
}
