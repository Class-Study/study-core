package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.chat.request.CreateChatMessageRequest;
import com.example.studycore.infrastructure.api.controllers.chat.request.MarkChatMessagesAsReadRequest;
import com.example.studycore.infrastructure.api.controllers.chat.response.ChatMessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/chat")
public interface ChatApi {


    @GetMapping("/{activityId}/messages")
    ResponseEntity<List<ChatMessageResponse>> listMessages(@PathVariable UUID activityId);


    @PostMapping("/{activityId}/messages")
    ResponseEntity<ChatMessageResponse> createMessage(
            @PathVariable UUID activityId,
            @RequestBody CreateChatMessageRequest request);


    @PatchMapping("/{activityId}/messages/read")
    ResponseEntity<Void> markAsRead(
            @PathVariable UUID activityId,
            @RequestBody MarkChatMessagesAsReadRequest request);
}
