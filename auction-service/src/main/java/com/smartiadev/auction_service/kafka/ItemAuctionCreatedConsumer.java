package com.smartiadev.auction_service.kafka;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.base_domain_service.dto.ItemAuctionCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ItemAuctionCreatedConsumer {

    private final AuctionRepository auctionRepository;

    @KafkaListener(
            topics = "item.auction.created"
    )
    @Transactional
    public void onItemAuctionCreated(ItemAuctionCreatedEvent event) {

        if (auctionRepository.existsByItemId(event.itemId())) {
            return;
        }

        Double reservePrice = event.reservePrice();

        if (reservePrice == null || reservePrice <= 0) {
            reservePrice = event.startPrice();
        }

        if (reservePrice < event.startPrice()) {
            throw new IllegalArgumentException(
                    "Reserve price must be greater than or equal to start price"
            );
        }

        Auction auction = Auction.builder()
                .itemId(event.itemId())
                .ownerId(event.ownerId())
                .startPrice(event.startPrice())
                .currentPrice(event.startPrice())
                .reservePrice(reservePrice)
                .views(0)
                .watchers(0)
                .startDate(LocalDateTime.now())
                .endDate(event.endDate())
                .status(AuctionStatus.DRAFT)
                .build();

        auctionRepository.save(auction);
    }
}
