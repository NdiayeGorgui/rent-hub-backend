package com.smartiadev.messaging_service.dto;

import java.util.UUID;

public record MessageRequest(
        UUID receiverId,
        Long itemId,
        String content
) {
}