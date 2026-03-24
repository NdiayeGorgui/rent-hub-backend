package com.smartiadev.auction_service.kafka;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionCancelledConsumer {
    private final AuctionRepository auctionRepository;

    @KafkaListener(topics = "auction.cancelled")
    @Transactional
    public void onAuctionCancelled(AuctionCancelledEvent event) {

        Auction auction = auctionRepository
                .findById(event.auctionId())
                .orElseThrow();

        // 🛑 idempotence
        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            return;
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        auctionRepository.save(auction);
    }
}