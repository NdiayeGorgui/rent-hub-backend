package com.smartiadev.dispute_service.kafka;

import com.smartiadev.base_domain_service.dto.DisputeCreatedEvent;
import com.smartiadev.base_domain_service.dto.ItemDeactivatedEvent;
import com.smartiadev.base_domain_service.dto.UserSuspendedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisputeEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void itemDeactivated(ItemDeactivatedEvent event) {
        kafkaTemplate.send("item.deactivated", event);
    }

    public void userSuspended(UserSuspendedEvent event) {
        kafkaTemplate.send("user.suspended", event);
    }

    public void disputeCreated(DisputeCreatedEvent event) {
        kafkaTemplate.send("dispute.created", event);
    }
}
