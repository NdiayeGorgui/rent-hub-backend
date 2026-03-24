package com.smartiadev.messaging_service.dto;

import java.util.UUID;

public record SupportMessageRequest(
        UUID receiverId, // null si user → admin
        String content
) {}