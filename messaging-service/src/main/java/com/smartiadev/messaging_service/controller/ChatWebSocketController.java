package com.smartiadev.messaging_service.controller;

import com.smartiadev.messaging_service.dto.MessageResponse;
import com.smartiadev.messaging_service.dto.SendMessageRequest;
import com.smartiadev.messaging_service.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageRequest request) {

        MessageResponse message = messageService.sendMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + message.conversationId(),
                message
        );
    }
}