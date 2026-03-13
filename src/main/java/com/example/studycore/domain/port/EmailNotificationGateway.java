package com.example.studycore.domain.port;

import com.example.studycore.domain.model.EmailNotification;

public interface EmailNotificationGateway {
    EmailNotification save(EmailNotification emailNotification);
}

