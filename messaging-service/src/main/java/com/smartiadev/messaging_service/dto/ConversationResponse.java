package com.smartiadev.messaging_service.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponse(

        Long id,
        UUID user1Id,
        UUID user2Id,
        String user1Username,
        String user2Username,
        Long itemId,

        String lastMessage,
        LocalDateTime lastMessageAt,

        UUID lastSenderId,

        long unreadCount

) {}
