package com.example.studycore.domain.port;

import com.example.studycore.domain.model.ChatMessage;
import java.util.List;
import java.util.UUID;

public interface ChatMessageGateway {

    /**
     * Busca todas as mensagens de uma atividade ordenadas por sent_at ASC
     */
    List<ChatMessage> findByActivityId(UUID activityId);

    /**
     * Salva uma nova mensagem
     */
    ChatMessage save(ChatMessage message);

    /**
     * Marca todas as mensagens não lidas de um usuário em uma atividade como lidas
     */
    void markAsRead(UUID activityId, UUID userId);
}

