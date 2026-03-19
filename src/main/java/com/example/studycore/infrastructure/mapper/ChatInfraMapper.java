package com.example.studycore.infrastructure.mapper;

import com.example.studycore.domain.model.ChatMessage;
import com.example.studycore.infrastructure.api.controllers.chat.response.ChatMessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ChatInfraMapper {

    ChatInfraMapper INSTANCE = Mappers.getMapper(ChatInfraMapper.class);

    ChatMessageResponse toChatMessageResponse(ChatMessage message);

    List<ChatMessageResponse> toChatMessageResponseList(List<ChatMessage> messages);
}

