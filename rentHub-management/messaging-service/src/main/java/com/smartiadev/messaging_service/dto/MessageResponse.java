package com.smartiadev.messaging_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        Long id,
        Long conversationId,
        UUID senderId,
        UUID receiverId,
        Long itemId,
        String content,
        LocalDateTime timestamp,
        boolean read
) {
}
