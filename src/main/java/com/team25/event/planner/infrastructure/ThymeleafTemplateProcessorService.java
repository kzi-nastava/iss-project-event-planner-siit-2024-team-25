package com.team25.event.planner.infrastructure;

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
    private final String frontendUrl;

    private final SpringTemplateEngine templateEngine;

    public ThymeleafTemplateProcessorService(
            @Value("${email.static.resource-url}") String resourceUrl,
            @Value("${frontend-url}") String frontendUrl,
            SpringTemplateEngine templateEngine
    ) {
        this.resourceUrl = resourceUrl;
        this.frontendUrl = frontendUrl;
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
        context.setVariable("loginUrl", frontendUrl + dto.getLoginUrlPath());

        return templateEngine.process("test", context);
    }
}
