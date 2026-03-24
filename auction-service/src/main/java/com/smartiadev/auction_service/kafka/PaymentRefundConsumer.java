package com.smartiadev.auction_service.kafka;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.base_domain_service.dto.AuctionFeeRefundedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRefundConsumer {
    private final AuctionRepository auctionRepository;

    @KafkaListener(topics = "auction.fee.refunded")
    @Transactional
    public void handleAuctionRefund(AuctionFeeRefundedEvent event) {

        Auction auction = auctionRepository
                .findByItemId(event.itemId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // 🛑 éviter double traitement Kafka
        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            return;
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        auctionRepository.save(auction);
    }
}