package com.smartiadev.payments_service.stripe;

import com.smartiadev.payments_service.dto.PaymentProviderResult;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class StripePaymentProvider implements PaymentProvider {

    @Override
    public PaymentProviderResult charge(UUID userId, Double amount) {

        try {
           /* PaymentIntent intent = PaymentIntent.create(
                    Map.of(
                            "amount", (long) (amount * 100),
                            "currency", "usd",
                            "metadata", Map.of(
                                    "userId", userId.toString()
                            )
                    )
            );*/
            PaymentIntent intent = PaymentIntent.create(
                    Map.of(
                            "amount", (long) (amount * 100),
                            "currency", "usd",
                            "automatic_payment_methods", Map.of(
                                    "enabled", true,
                                    "allow_redirects", "never"   // 👈 AJOUT IMPORTANT
                            ),
                            "metadata", Map.of(
                                    "userId", userId.toString()
                            )
                    )
            );


            return new PaymentProviderResult(
                    true,
                    intent.getId(),
                    intent.getClientSecret(),   // 👈 ICI
                    null
            );

        } catch (StripeException e) {
            return new PaymentProviderResult(
                    false,
                    null,
                    null,
                    e.getMessage()
            );
        }
    }

    @Override
    public PaymentProviderResult refund(String paymentIntentId, Double amount) {

        try {

            Refund refund = Refund.create(
                    Map.of(
                            "payment_intent", paymentIntentId,
                            "amount", (long) (amount * 100)
                    )
            );

            return new PaymentProviderResult(
                    true,
                    refund.getId(),
                    null,
                    null
            );

        } catch (StripeException e) {

            return new PaymentProviderResult(
                    false,
                    null,
                    null,
                    e.getMessage()
            );
        }
    }
}

