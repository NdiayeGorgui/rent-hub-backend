package com.smartiadev.payments_service.dto;

public record PaymentProviderResult(
        boolean success,
        String transactionId,
        String clientSecret,   // 👈 AJOUT ICI
        String failureReason
) {}

