package com.smartiadev.auction_service.dto;

import java.time.LocalDateTime;

public record AuctionDto(
        Long id,
        Long itemId,
        Double startPrice,
        Double currentPrice,
        Integer participantsCount,
        Integer views,
        Integer watchers,
        LocalDateTime endDate,
        String status,
        boolean reserveReached
) {}

