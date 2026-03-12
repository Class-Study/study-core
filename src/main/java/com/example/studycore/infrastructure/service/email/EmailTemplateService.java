package com.example.studycore.infrastructure.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public String render(String templateName, Map<String, Object> variables) {
        try {
            final var context = new Context();
            context.setVariables(variables);

            final var renderedTemplate = templateEngine.process(templateName, context);
            log.debug("Template {} rendered successfully with {} variables", templateName, variables.size());

            return renderedTemplate;
        } catch (Exception e) {
            log.error("Failed to render template: {} with variables: {}", templateName, variables, e);
            throw new RuntimeException("Failed to render email template: " + templateName, e);
        }
    }
}
