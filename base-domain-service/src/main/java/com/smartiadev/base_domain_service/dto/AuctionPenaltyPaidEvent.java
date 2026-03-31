package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record AuctionPenaltyPaidEvent(
        UUID winnerId,
        Long auctionId,
        Double amount
) {}
