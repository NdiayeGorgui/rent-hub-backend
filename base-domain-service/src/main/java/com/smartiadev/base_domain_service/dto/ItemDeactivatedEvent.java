package com.smartiadev.base_domain_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ItemDeactivatedEvent(
        Long itemId,
        Long disputeId,
        UUID adminId,
        String decision,
        LocalDateTime occurredAt
) {}

