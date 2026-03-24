package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.ItemDeactivatedEvent;
import com.smartiadev.item_service.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisputeItemDeactivatedConsumer {
    private final ItemService itemService;


    @KafkaListener(topics = "item.deactivated", groupId = "item-service")
    public void onItemDeactivated(ItemDeactivatedEvent event) {
        itemService.adminDeactivate(event.itemId());
    }

}
