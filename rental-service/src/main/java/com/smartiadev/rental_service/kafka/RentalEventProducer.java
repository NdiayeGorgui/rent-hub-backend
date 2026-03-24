package com.smartiadev.rental_service.kafka;


import com.smartiadev.base_domain_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class RentalEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRentalApproved(RentalApprovedEvent event) {
        kafkaTemplate.send("rental.approved", event);
    }

    public void sendRentalStarted(RentalStartedEvent event) {
        kafkaTemplate.send("rental.started", event);
    }

    public void sendRentalCancelled(RentalCancelledEvent event) {
        kafkaTemplate.send("rental.cancelled", event);
    }

    public void sendRentalCancelledByUser(RentalCancelledByUserEvent event) {
        kafkaTemplate.send("rental.cancelled.by.user", event);
    }

    public void sendRentalEnded(RentalEndedEvent event) {
        kafkaTemplate.send("rental.ended", event);
    }

    public void sendRentalRequestExpired(RentalRequestExpiredEvent event) {
        kafkaTemplate.send("rental.request.expired", event);
    }

}



