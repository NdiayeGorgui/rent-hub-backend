package com.smartiadev.base_domain_service.dto;

public record AuctionExpiredCleanupEvent(
        Long auctionId,
        Long itemId
) {}
