package com.smartiadev.item_service.dto;

import java.time.LocalDateTime;

public record PremiumStatusResponse(
        boolean premium,
        LocalDateTime endDate,
        boolean gracePeriod
) {}
