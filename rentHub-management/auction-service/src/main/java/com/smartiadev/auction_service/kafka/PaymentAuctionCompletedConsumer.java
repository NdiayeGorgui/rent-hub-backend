/*
package com.smartiadev.auction_service.kafka;


import com.smartiadev.auction_service.service.AuctionService;
import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.base_domain_service.dto.SubscriptionRenewalRequestedEvent;
import com.smartiadev.base_domain_service.model.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAuctionCompletedConsumer {

private final AuctionService auctionService;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "auction-service"
    )
    public void onPaymentCompleted(PaymentCompletedEvent event) {

        if (event.type() != PaymentType.AUCTION_FEE) {
            return;
        }

        auctionService.createAuctionAfterPayment(event);
    }
}
*/
