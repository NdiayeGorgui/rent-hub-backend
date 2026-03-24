package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import com.smartiadev.base_domain_service.dto.AuctionFeeRefundedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Component
@RequiredArgsConstructor
public class AuctionRefundedConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "auction.fee.refunded",
            groupId = "notification-group"
    )
    public void onAuctionRefunded(AuctionFeeRefundedEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.ownerId(),
                        "💸 Votre paiement de " +event.amount()+"$"+"pour la création de l'enchére a été remboursé.",
                        "AUCTION_REFUNDED",
                        false,
                        LocalDateTime.now()
                )
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.ownerId(),
                notification
        );
    }
}