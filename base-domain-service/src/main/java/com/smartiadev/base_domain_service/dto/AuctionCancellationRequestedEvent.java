package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record AuctionCancellationRequestedEvent(
        Long auctionId,
        Long itemId,
        UUID ownerId,
        Double amount
) {}
