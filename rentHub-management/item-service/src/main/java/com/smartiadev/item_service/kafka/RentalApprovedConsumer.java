package com.smartiadev.item_service.kafka;



import com.smartiadev.base_domain_service.dto.RentalApprovedEvent;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemStatus;
import com.smartiadev.item_service.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalApprovedConsumer {

    private final ItemRepository itemRepository;

    @KafkaListener(
            topics = "rental.approved",
            groupId = "item-service-group"
    )
    public void consume(RentalApprovedEvent event) {

        Item item = itemRepository.findById(event.itemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 🔒 verrouillage
        item.setActive(false);
        item.setStatus(ItemStatus.INACTIVE);

        itemRepository.save(item);

        System.out.println("🔒 Item locked after rental approval: " + event.itemId());
    }
}

