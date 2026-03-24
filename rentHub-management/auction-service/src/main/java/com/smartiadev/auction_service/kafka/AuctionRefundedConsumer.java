/*
package com.smartiadev.auction_service.kafka;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import com.smartiadev.base_domain_service.dto.AuctionFeeRefundedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionRefundedConsumer {
    private final AuctionRepository auctionRepository;

    @KafkaListener(topics = "auction.fee.refunded")
    @Transactional
    public void onAuctionCancelled(AuctionFeeRefundedEvent event) {

        Auction auction = auctionRepository
                .findById(event.auctionId())
                .orElseThrow();

        // 🛑 idempotence
        if (auction.getStatus() != AuctionStatus.CLOSED) {
            return;
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        auctionRepository.save(auction);
    }
}*/
