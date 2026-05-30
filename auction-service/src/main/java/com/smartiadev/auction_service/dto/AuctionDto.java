package com.smartiadev.auction_service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuctionDto(
        Long id,
        Long itemId,
        UUID ownerId,
        UUID winnerId,
        Double startPrice,
        Double currentPrice,
        Integer participantsCount,
        Integer views,
        Integer watchers,
        LocalDateTime endDate,
        String status,
        boolean reserveReached,
        // 🔥 ITEM
        String itemTitle,
        List<String> itemImages,

        // 🔥 USERS
        String ownerUsername,
        String winnerUsername
) {}

