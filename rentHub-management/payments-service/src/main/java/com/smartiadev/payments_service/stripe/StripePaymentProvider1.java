/*
package com.smartiadev.payments_service.stripe;

import com.smartiadev.payments_service.dto.PaymentProviderResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("stripe")
public class StripePaymentProvider1 implements PaymentProvider {

    @Override
    public PaymentProviderResult charge(UUID userId, Double amount) {

        // Stripe API plus tard
        return new PaymentProviderResult(
                true,
                "STRIPE_TX_123",
                null
        );
    }
}

*/
