package com.smartiadev.subscription_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentFailedEvent;
import com.smartiadev.subscription_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final SubscriptionService service;

    @KafkaListener(
            topics = "payment.failed",
            groupId = "subscription-group"
    )
    public void onPaymentFailed(PaymentFailedEvent event) {

        service.handlePaymentFailure(event.userId());

    }
}