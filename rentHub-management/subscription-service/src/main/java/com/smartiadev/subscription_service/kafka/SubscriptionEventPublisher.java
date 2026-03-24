package com.smartiadev.subscription_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionRenewalRequestedEvent;
import com.smartiadev.base_domain_service.dto.SubscriptionRenewedEvent;
import com.smartiadev.base_domain_service.dto.SubscriptionGracePeriodStartedEvent;
import com.smartiadev.base_domain_service.model.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Event envoyé quand on demande un renouvellement
     * subscription-service -> payment-service
     */
    public void publishRenewalRequested(UUID userId, LocalDateTime endDate) {

        kafkaTemplate.send(
                "subscription.renewal.requested",
                new SubscriptionRenewalRequestedEvent(
                        userId,
                        endDate
                )
        );
    }

    /**
     * Event envoyé quand un abonnement est renouvelé
     * subscription-service -> notification-service
     */
    public void publishRenewed(UUID userId, LocalDateTime newEndDate) {

        kafkaTemplate.send(
                "subscription.renewed",
                new SubscriptionRenewedEvent(
                        userId,
                        newEndDate,
                        PaymentType.SUBSCRIPTION
                )
        );
    }

    /**
     * Event envoyé quand un GRACE PERIOD commence
     * subscription-service -> notification-service
     */
    public void publishGracePeriodStarted(UUID userId, LocalDateTime graceEndDate) {

        kafkaTemplate.send(
                "subscription.grace.started",
                new SubscriptionGracePeriodStartedEvent(
                        userId,
                        graceEndDate
                )
        );
    }

    /**
     * Event envoyé quand un abonnement arrive à expiration
     * subscription-service -> notification-service
     */
    public void publishExpired(UUID userId, LocalDateTime expiredAt) {
        kafkaTemplate.send(
                "subscription.expired",
                new com.smartiadev.base_domain_service.dto.SubscriptionExpiredEvent(
                        userId,
                        expiredAt
                )
        );
    }
}