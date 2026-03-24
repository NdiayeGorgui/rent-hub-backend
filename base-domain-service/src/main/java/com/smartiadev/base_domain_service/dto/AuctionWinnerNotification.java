package com.smartiadev.base_domain_service.dto;


import java.util.UUID;

public record AuctionWinnerNotification(
        Long auctionId,
        Long itemId,
        UUID winnerId,
        Double winningAmount
) {}

