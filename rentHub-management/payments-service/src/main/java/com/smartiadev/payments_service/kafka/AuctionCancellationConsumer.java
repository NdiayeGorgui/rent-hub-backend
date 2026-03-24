package com.smartiadev.payments_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionCancellationRequestedEvent;
import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import com.smartiadev.base_domain_service.dto.SubscriptionRenewalRequestedEvent;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Component
@RequiredArgsConstructor
public class AuctionCancellationConsumer {

    private final PaymentService service;

    @KafkaListener(
            topics = "auction.cancellation.requested",
            groupId = "payment-group"
    )
    public void handleCancellation(AuctionCancellationRequestedEvent event) {

        log.info("📥 Received auction.cancellation.requested → auctionId={}, itemId={}, ownerId={}, amount={}",
                event.auctionId(),
                event.itemId(),
                event.ownerId(),
                event.amount()
        );

        // ❌ ON NE CRÉE PLUS LE PAIEMENT ICI
        log.info("⚠️ Ignored: payment must be triggered via REST (Stripe PaymentIntent)");
    }
}