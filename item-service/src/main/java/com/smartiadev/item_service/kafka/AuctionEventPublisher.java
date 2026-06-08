package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionFeePaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAuctionFeePaid(
            AuctionFeePaidEvent event
    ) {

        kafkaTemplate.send(
                "auction.fee.paid",
                event.itemId().toString(),
                event
        );
    }
}
