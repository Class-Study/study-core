package com.example.studycore.infrastructure.service.email;

import com.example.studycore.application.facades.MessageFacade;
import com.example.studycore.application.messages.NotifyUserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyEmailService {

    private final EmailTemplateService emailTemplateService;
    private final MessageFacade messageFacade;

    public void sendWelcomeTeacher(String toEmail, String name, String temporaryPassword) {
        try {
            final var variables = Map.<String, Object>of(
                    "name",     name,
                    "email",    toEmail,
                    "password", temporaryPassword
            );

            final var body = emailTemplateService.render("email-create-user", variables);

            messageFacade.sendMessage(new NotifyUserMessage(
                    toEmail,
                    "Bem-vindo ao EduSpace — suas credenciais de acesso",
                    body
            ));

            log.info("Welcome teacher email queued successfully for: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to queue welcome teacher email for: {}", toEmail, e);
            // Note: Not throwing exception to avoid breaking the teacher creation process
            // In a production environment, you might want to add this to a retry mechanism
        }
    }

    public void sendWelcomeStudent(String toEmail, String name, String teacherName, String temporaryPassword) {
        try {
            final var variables = Map.<String, Object>of(
                    "name", name,
                    "email", toEmail,
                    "password", temporaryPassword,
                    "teacherName", teacherName
            );

            final var body = emailTemplateService.render("email-create-user", variables);

            messageFacade.sendMessage(new NotifyUserMessage(
                    toEmail,
                    "Bem-vindo ao EduSpace — suas credenciais de acesso",
                    body
            ));

            log.info("Welcome student email queued successfully for: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to queue welcome student email for: {}", toEmail, e);
        }
    }

    public void sendPasswordReset(String toEmail, String name, String resetToken) {
        try {
            final var variables = Map.<String, Object>of(
                    "name",       name,
                    "email",      toEmail,
                    "resetToken", resetToken,
                    "resetUrl",   "http://localhost:3000/reset-password?token=" + resetToken
            );

            final var body = emailTemplateService.render("email-password-reset", variables);

            messageFacade.sendMessage(new NotifyUserMessage(
                    toEmail,
                    "EduSpace — Recuperação de senha",
                    body
            ));

            log.info("Password reset email queued successfully for: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to queue password reset email for: {}", toEmail, e);
        }
    }

    public void sendBillingNotification(String toEmail, String name, String billingDetails) {
        try {
            final var variables = Map.<String, Object>of(
                    "name",           name,
                    "email",          toEmail,
                    "billingDetails", billingDetails
            );

            final var body = emailTemplateService.render("email-billing-notification", variables);

            messageFacade.sendMessage(new NotifyUserMessage(
                    toEmail,
                    "EduSpace — Notificação de cobrança",
                    body
            ));

            log.info("Billing notification email queued successfully for: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to queue billing notification email for: {}", toEmail, e);
        }
    }

    // Padrão para novos e-mails:
    // 1. Defina as variáveis do template em Map.of(...)
    // 2. Chame emailTemplateService.render(nomeDoTemplate, variables)
    // 3. Chame messageFacade.sendMessage(new NotifyUserMessage(...))
}
