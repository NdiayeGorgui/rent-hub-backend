package com.smartiadev.subscription_service.service;

import com.smartiadev.subscription_service.dto.SubscriptionStats;
import com.smartiadev.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionStatsService {

    private final SubscriptionRepository repository;

    public SubscriptionStats getStats() {
        return new SubscriptionStats(
                repository.count(),
                repository.countByEndDateAfter(LocalDateTime.now()),
                repository.countByEndDateBefore(LocalDateTime.now()),
                repository.countByAutoRenewTrue()

        );
    }
}

