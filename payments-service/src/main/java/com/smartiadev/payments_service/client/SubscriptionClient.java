package com.smartiadev.payments_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/subscriptions/internal/{userId}/is-premium")
    boolean isPremium(@PathVariable UUID userId);

    @PostMapping("/api/subscriptions/internal/subscribe/{userId}")
    void subscribeInternal(@PathVariable UUID userId);
}



