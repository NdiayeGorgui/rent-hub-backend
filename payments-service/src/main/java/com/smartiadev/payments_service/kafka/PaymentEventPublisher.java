package com.smartiadev.payments_service.kafka;


import com.smartiadev.base_domain_service.dto.AuctionFeeRefundedEvent;
import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.base_domain_service.dto.PaymentCreatedEvent;
import com.smartiadev.base_domain_service.dto.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCreated(PaymentCreatedEvent event) {
        kafkaTemplate.send("payment.created", event);
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        kafkaTemplate.send("payment.completed", event);
    }

    public void publishPaymentFailed(UUID userId, String reason) {
        kafkaTemplate.send(
                "payment.failed",
                new PaymentFailedEvent(
                        userId,
                        reason,
                        LocalDateTime.now()
                )
        );
    }

    public void publishAuctionRefunded(AuctionFeeRefundedEvent event) {

        kafkaTemplate.send(
                "auction.fee.refunded",
                event.itemId().toString(),
                event
        );
    }
}

