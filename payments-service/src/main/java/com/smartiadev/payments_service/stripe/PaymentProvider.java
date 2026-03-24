package com.smartiadev.payments_service.stripe;

import com.smartiadev.payments_service.dto.PaymentProviderResult;

import java.util.UUID;

public interface PaymentProvider {

    PaymentProviderResult charge(
            UUID userId,
            Double amount
    );

    PaymentProviderResult refund(String paymentIntentId, Double amount);
}

