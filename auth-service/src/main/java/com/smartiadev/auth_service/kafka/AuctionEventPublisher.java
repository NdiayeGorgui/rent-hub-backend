package com.smartiadev.auth_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionStrikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAuctionStrike(AuctionStrikeEvent event) {

        kafkaTemplate.send(
                "auction.strike",
                event.userId().toString(),
                event
        );

        System.out.println(
                "⚠️ Auction strike event sent for user " + event.userId()
        );
    }
}