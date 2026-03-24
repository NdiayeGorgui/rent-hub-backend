package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemStatus;
import com.smartiadev.item_service.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionCancelledConsumer {
    private final ItemRepository itemRepository;

    @KafkaListener(topics = "auction.cancelled")
    @Transactional
    public void onAuctionCancelled(AuctionCancelledEvent event) {

        Item item = itemRepository
                .findById(event.itemId())
                .orElseThrow();

        // 🛑 idempotence
        if (item.getStatus() == ItemStatus.CANCELLED_AUCTION) {
            return;
        }

        item.setStatus(ItemStatus.CANCELLED_AUCTION);
        item.setActive(false);

        itemRepository.save(item);
    }
}
