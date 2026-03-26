package com.smartiadev.subscription_service.service;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.subscription_service.dto.PremiumStatusResponse;
import com.smartiadev.subscription_service.entity.Subscription;
import com.smartiadev.subscription_service.entity.SubscriptionStatus;
import com.smartiadev.subscription_service.kafka.SubscriptionEventPublisher;
import com.smartiadev.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionEventPublisher eventPublisher;

    public boolean isPremium(UUID userId) {

        return repository.findByUserId(userId)
                .map(Subscription::isPremiumActive)
                .orElse(false);
    }

    public PremiumStatusResponse getPremiumStatus(UUID userId) {

        return repository.findByUserId(userId)
                .map(sub -> new PremiumStatusResponse(
                        sub.isPremiumActive(),
                        sub.getEndDate(),
                        sub.getStatus() == SubscriptionStatus.GRACE_PERIOD
                ))
                .orElse(new PremiumStatusResponse(
                        false,
                        null,
                        false
                ));
    }

    public Subscription subscribe(UUID userId, String paymentRef, Long paymentid) {

        repository.findByUserId(userId).ifPresent(sub -> {
            if (sub.getStatus() == SubscriptionStatus.ACTIVE) {
                throw new IllegalStateException("Already subscribed");
            }
        });

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(6))
                .autoRenew(true)
                .paymentRef(paymentRef)
                .paymentId(paymentid)
                .build();

        return repository.save(subscription);
    }

    public void renew(UUID userId) {

        var sub = repository.findByUserId(userId)
                .orElseThrow();

        sub.setEndDate(
                sub.getEndDate().isBefore(LocalDateTime.now())
                        ? LocalDateTime.now().plusMonths(6)
                        : sub.getEndDate().plusMonths(6)
        );
        sub.setStatus(SubscriptionStatus.ACTIVE);

        repository.save(sub);

        eventPublisher.publishRenewed(
                sub.getUserId(),
                sub.getEndDate()
        );
    }

    public void handlePaymentCompleted(PaymentCompletedEvent event) {

        if (event.type() != PaymentType.SUBSCRIPTION &&
                event.type() != PaymentType.SUBSCRIPTION_RENEWAL) {
            return;
        }

        if (repository.findByUserId(event.userId()).isPresent()) {

            renew(event.userId());

        } else {

            subscribe(
                    event.userId(),
                    event.paymentIntentId(),
                    event.paymentId()
            );
        }
    }
    public void cancel(UUID userId) {

        Subscription sub = repository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Subscription not found"));

        if (sub.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Subscription already cancelled");
        }

        sub.setAutoRenew(false);
        sub.setCancelledAt(LocalDateTime.now());

        repository.save(sub);
    }

    public void handlePaymentFailure(UUID userId) {

        repository.findByUserId(userId).ifPresent(sub -> {

            sub.setStatus(SubscriptionStatus.GRACE_PERIOD);

            // 3 jours pour corriger le paiement
            sub.setEndDate(LocalDateTime.now().plusDays(3));

            repository.save(sub);

            eventPublisher.publishGracePeriodStarted(
                    sub.getUserId(),
                    sub.getEndDate()
            );
        });
    }

}
