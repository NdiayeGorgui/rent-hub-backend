package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.RentalEndedEvent;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemStatus;
import com.smartiadev.item_service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RentalEndedConsumer {

    private final ItemRepository itemRepository;

    @KafkaListener(topics = "rental.ended")
    @Transactional
    public void handleRentalEnded(RentalEndedEvent event) {

        Item item = itemRepository.findById(event.getItemId())
                .orElseThrow(() ->
                        new IllegalStateException("Item not found"));

        // 🔓 rendu disponible
        item.setStatus(ItemStatus.ACTIVE);
        item.setActive(true);

        itemRepository.save(item);

        System.out.println(
                "✅ Item " + item.getId() + " est maintenant DISPONIBLE"
        );
    }
}
