package com.smartiadev.messaging_service.controller;

import com.smartiadev.messaging_service.dto.ConversationResponse;
import com.smartiadev.messaging_service.dto.MessageRequest;
import com.smartiadev.messaging_service.dto.MessageResponse;
import com.smartiadev.messaging_service.dto.SupportMessageRequest;
import com.smartiadev.messaging_service.entity.Conversation;
import com.smartiadev.messaging_service.entity.Message;
import com.smartiadev.messaging_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 📩 envoyer message
    @PostMapping("/send")
    public MessageResponse sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody MessageRequest request
    ) {

        UUID senderId = UUID.fromString(jwt.getSubject());

        return messageService.sendMessage(senderId, request);
    }

    // 💬 récupérer messages d'une conversation
    @GetMapping("/conversation/{conversationId}")
    public List<MessageResponse> getConversationMessages(
            @PathVariable Long conversationId
    ) {
        return messageService.getConversationMessages(conversationId);
    }

    // 📬 récupérer les conversations d'un utilisateur
    @GetMapping("/conversations")
    public List<ConversationResponse> getUserConversations(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return messageService.getUserConversations(userId);
    }

    // ✔ marquer message comme lu
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {

        messageService.markAsRead(id);
    }

    // 🛠 message au support
    @PostMapping("/support")
    public ResponseEntity<MessageResponse> sendSupportMessage(
            @RequestBody SupportMessageRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID senderId = UUID.fromString(jwt.getSubject());

        System.out.println("==== SUPPORT DEBUG ====");
        System.out.println("RAW BODY: " + request);
        System.out.println("receiverId: " + request.receiverId());
        System.out.println("content: " + request.content());

        return ResponseEntity.ok(
                messageService.sendSupportMessage(senderId, request)
        );
    }

    @GetMapping("/unread-count")
    public long getUnreadMessagesCount(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        return messageService.getUnreadMessagesCount(userId);

    }

}