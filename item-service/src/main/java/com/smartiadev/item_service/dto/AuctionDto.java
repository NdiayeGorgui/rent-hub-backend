package com.smartiadev.item_service.dto;

import java.time.LocalDateTime;

public record AuctionDto(
        Long id,
        Long itemId,
        Double currentPrice,
        LocalDateTime endDate,
        String status
) {}

