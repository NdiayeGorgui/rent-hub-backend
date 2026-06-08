package com.smartiadev.item_service.kafka;


import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.base_domain_service.dto.ItemAuctionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAuctionCreated(ItemAuctionCreatedEvent event) {
        kafkaTemplate.send("item.auction.created", event);
    }


}
