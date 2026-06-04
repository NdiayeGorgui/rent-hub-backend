package com.smartiadev.subscription_service.dto;

import java.util.UUID;

public record PremiumUserStatusDto(
        UUID userId,
        boolean premium,
        boolean gracePeriod
) {}