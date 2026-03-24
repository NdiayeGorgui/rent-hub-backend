package com.smartiadev.base_domain_service.dto;


import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.base_domain_service.model.PaymentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEvent(
        Long paymentId,
        String paymentIntentId,
        UUID userId,
        Long itemId,
        Double amount,
        PaymentType type,
        PaymentStatus paymentStatus,
        LocalDateTime paidAt
) {}
