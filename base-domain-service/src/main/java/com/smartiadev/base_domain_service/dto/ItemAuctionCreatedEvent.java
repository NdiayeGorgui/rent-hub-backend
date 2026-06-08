package com.smartiadev.base_domain_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ItemAuctionCreatedEvent(
        Long itemId,
        UUID ownerId,
        Double startPrice,
        Double reservePrice,
        LocalDateTime endDate
) {}
