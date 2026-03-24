package com.smartiadev.subscription_service.dto;

public record SubscriptionStats(
        Long totalSubscriptions,
        Long activeSubscriptions,
        Long expiredSubscriptions,
        Long autoRenewEnabled
) {}

