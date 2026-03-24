package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.item_service.entity.ItemStatus;
import com.smartiadev.item_service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionClosedItemConsumer {

    private final ItemRepository itemRepository;

    @KafkaListener(
            topics = "auction.closed",
            groupId = "item-group"
    )
    public void onAuctionClosed(AuctionClosedEvent event) {

        itemRepository.findById(event.itemId())
                .ifPresent(item -> {
                    item.setStatus(ItemStatus.INACTIVE);
                    item.setActive(false);
                    itemRepository.save(item);
                });
    }
}

