package com.smartiadev.base_domain_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID userId,
        String reason,
        LocalDateTime occurredAt
) {}
