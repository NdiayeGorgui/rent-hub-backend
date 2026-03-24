package com.smartiadev.auction_service.kafka;


import com.smartiadev.base_domain_service.dto.AuctionBidPlacedEvent;
import com.smartiadev.base_domain_service.dto.AuctionCancellationRequestedEvent;
import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAuctionClosed(AuctionClosedEvent event) {
        kafkaTemplate.send("auction.closed", event);
    }

    public void publishBidPlaced(AuctionBidPlacedEvent event) {
        kafkaTemplate.send("auction.bid.placed", event);
    }

    public void publishAuctionCancellationRequested(AuctionCancellationRequestedEvent event) {

        kafkaTemplate.send(
                "auction.cancellation.requested",
                String.valueOf(event.auctionId()), // key
                event
        );
    }
}
