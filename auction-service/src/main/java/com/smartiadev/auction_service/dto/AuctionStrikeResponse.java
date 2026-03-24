package com.smartiadev.auction_service.dto;


import lombok.Builder;

@Builder
public record AuctionStrikeResponse(
        int auctionStrikes,
        boolean auctionRestricted
) {}
