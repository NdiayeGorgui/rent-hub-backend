package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.dto.PremiumStatusResponse;
import com.smartiadev.auth_service.dto.SubscriptionStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/subscriptions/internal/{userId}/is-premium")
    boolean isPremium(@PathVariable UUID userId);

    @GetMapping("/api/subscriptions/internal/stats")
    SubscriptionStats getStats();

    @GetMapping("/api/subscriptions/internal/{userId}/status")
    PremiumStatusResponse getPremiumStatus(@PathVariable UUID userId);
}

