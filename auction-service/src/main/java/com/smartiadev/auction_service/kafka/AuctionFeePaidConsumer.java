package com.smartiadev.auction_service.kafka;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.base_domain_service.dto.AuctionFeePaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionFeePaidConsumer {

    private final AuctionRepository auctionRepository;

    @KafkaListener(
            topics = "auction.fee.paid"
    )
    public void onAuctionFeePaid(AuctionFeePaidEvent event) {

        Auction auction = auctionRepository.findByItemId(event.itemId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Auction not found for item "
                                        + event.itemId()
                        )
                );

        if (auction.getStatus() == AuctionStatus.OPEN) {
            return;
        }

        auction.setStatus(AuctionStatus.OPEN);

        auctionRepository.save(auction);

        log.info(
                "Auction opened for item {}",
                event.itemId()
        );
    }
}