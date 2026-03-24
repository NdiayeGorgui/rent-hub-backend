package com.smartiadev.base_domain_service.dto;


import java.util.UUID;

public record AuctionClosedEvent(
        Long auctionId,
        Long itemId,
        UUID ownerId,
        UUID winnerId,       // null si pas de gagnant
        Double winningAmount, // null si pas de gagnant
        boolean reserveMet
) {}

