package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record AuctionPenaltyEvent(
        UUID winnerId,
        Long auctionId,
        Long itemId,
        Double amount,
        String reason
) {}
