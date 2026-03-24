package com.smartiadev.auth_service.dto;

import java.util.UUID;

public record SubscriptionResponse(
        UUID userId,
        String status
) {}
