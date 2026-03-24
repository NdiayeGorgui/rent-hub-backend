/*
package com.smartiadev.payments_service.stripe;

import com.smartiadev.payments_service.dto.PaymentProviderResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FakePaymentProvider implements PaymentProvider {

    @Override
    public PaymentProviderResult charge(UUID userId, Double amount) {

        return new PaymentProviderResult(
                true,
                "FAKE_TX_" + System.currentTimeMillis(),
                null
        );
    }
}

*/
