package com.smartiadev.payments_service.dto;

import com.smartiadev.base_domain_service.model.PaymentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        Long id,
        Long itemId,
        Long auctionId,
        UUID userId,         // ✅ AJOUT
        String userFullName,
        Double amount,
        PaymentType paymentType,
        String status,
        LocalDateTime createdAt,
        String paymentIntentId,
        String clientSecret,
        boolean alreadyRefunded
) {}