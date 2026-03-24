package com.smartiadev.auction_service.service;

import com.smartiadev.auction_service.dto.AuctionStats;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionStatsService {

    private final AuctionRepository auctionRepository;

    public AuctionStats getStats() {
        return new AuctionStats(
                auctionRepository.count(),
                auctionRepository.countByStatus(AuctionStatus.OPEN),
                auctionRepository.countByStatus(AuctionStatus.CLOSED),
                auctionRepository.countAuctionsWithWinner(),
                auctionRepository.countAuctionsWithoutBid(),
                auctionRepository.averageWinningPrice()
        );
    }
}

