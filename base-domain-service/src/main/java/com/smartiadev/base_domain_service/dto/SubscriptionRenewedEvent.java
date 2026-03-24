package com.smartiadev.base_domain_service.dto;

import com.smartiadev.base_domain_service.model.PaymentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionRenewedEvent(
        UUID userId,
        LocalDateTime newEndDate,
        PaymentType paymentType
) {}
