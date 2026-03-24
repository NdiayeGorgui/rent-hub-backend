package com.smartiadev.auction_service.dto;

import java.time.LocalDateTime;

public record UpdateAuctionRequest(
        Double startPrice,
        Double reservePrice,
        LocalDateTime endDate
) {}
