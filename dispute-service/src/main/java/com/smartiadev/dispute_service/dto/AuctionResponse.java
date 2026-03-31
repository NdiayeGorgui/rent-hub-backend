package com.smartiadev.dispute_service.dto;

import java.util.UUID;

public record AuctionResponse(
        Long id,
        Long itemId,
        UUID ownerId,
        UUID winnerId,
        String status
) {}
