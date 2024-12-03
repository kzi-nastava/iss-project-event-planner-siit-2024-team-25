package com.team25.event.planner.infrastructure;

import com.team25.event.planner.email.dto.ActivationEmailBodyDTO;
import com.team25.event.planner.email.dto.TestEmailBodyDTO;
import com.team25.event.planner.email.service.TemplateProcessorService;
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
}
