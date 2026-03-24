package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record MessageSentEvent(
        Long conversationId,
        UUID senderId,
        UUID  receiverId,
        String content
) {}
