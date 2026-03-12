package com.example.studycore.application.facades;

import com.example.studycore.application.messages.NotifyUserMessage;

public interface MessageFacade {
    void sendMessage(NotifyUserMessage message);
}
