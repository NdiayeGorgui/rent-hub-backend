package com.smartiadev.payments_service.dto;

public record PaymentIntentResponse(
        String clientSecret,
        String paymentIntentId
) {}
