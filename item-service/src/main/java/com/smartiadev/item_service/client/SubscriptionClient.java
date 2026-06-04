package com.smartiadev.item_service.client;

import com.smartiadev.item_service.dto.PremiumStatusResponse;
import com.smartiadev.item_service.dto.PremiumUserStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/subscriptions/internal/{userId}/status")
    PremiumStatusResponse getPremiumStatus(@PathVariable UUID userId);

    @PostMapping("/api/subscriptions/internal/statuses")
    List<PremiumUserStatusDto> getPremiumStatuses(
            @RequestBody List<UUID> userIds
    );
}