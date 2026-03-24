package com.smartiadev.auth_service.dto;

public record UserBidEligibilityResponse(
        boolean canBid,
        boolean enabled,
        boolean auctionRestricted,
        int auctionStrikes
) {}
