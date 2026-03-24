package com.smartiadev.auction_service.dto;

public record AuctionStats(
        Long totalAuctions,
        Long openAuctions,
        Long closedAuctions,
        Long auctionsWithWinner,
        Long auctionsWithoutBid,
        Double avgWinningPrice
) {}

