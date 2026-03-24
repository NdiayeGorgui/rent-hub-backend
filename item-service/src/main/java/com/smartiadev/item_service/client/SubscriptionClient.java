package com.smartiadev.item_service.client;

import com.smartiadev.item_service.dto.PremiumStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/subscriptions/internal/{userId}/status")
    PremiumStatusResponse getPremiumStatus(@PathVariable UUID userId);
}