package com.smartiadev.base_domain_service.dto;


import java.util.UUID;

public record AuctionBidPlacedEvent(
        Long auctionId,
        Long itemId,
        UUID bidderId,
        Double amount
) {}
