package com.smartiadev.subscription_service.job;

import com.smartiadev.subscription_service.entity.Subscription;
import com.smartiadev.subscription_service.kafka.SubscriptionEventPublisher;
import com.smartiadev.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionRenewalJob {

    private final SubscriptionRepository repository;
    private final SubscriptionEventPublisher publisher;

    @Scheduled(cron = "0 0 2 * * *")
   //@Scheduled(cron = "0 06 11 * * *")
    public void renewSubscriptions() {

        List<Subscription> expiring =
                repository.findByEndDateBeforeAndAutoRenewTrue(LocalDateTime.now());

        for (Subscription sub : expiring) {

            publisher.publishRenewalRequested(
                    sub.getUserId(),
                    sub.getEndDate()
            );
        }
    }
}