package com.smartiadev.subscription_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.subscription_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final SubscriptionService service;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "subscription-group"
    )
    public void onPaymentCompleted(PaymentCompletedEvent event) {

        // déléguer la logique au service
        service.handlePaymentCompleted(event);

    }
}

