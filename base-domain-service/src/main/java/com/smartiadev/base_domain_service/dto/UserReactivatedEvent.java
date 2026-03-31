package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record UserReactivatedEvent(
        UUID userId,
        String reason
) {}

