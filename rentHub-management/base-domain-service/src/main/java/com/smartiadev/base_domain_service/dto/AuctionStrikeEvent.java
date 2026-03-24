package com.smartiadev.base_domain_service.dto;


import java.util.UUID;

public record AuctionStrikeEvent(
        UUID userId,
        int strikes,
        boolean restricted
) {}
