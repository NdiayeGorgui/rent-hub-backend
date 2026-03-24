package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionRefundEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentRefundConsumer {
    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "auction.refund",
            groupId = "notification-group"
    )
    public void onAuctionRefund(AuctionRefundEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.ownerId(),
                        "💰 Vos frais d'administration pour l'enchère "
                                + event.auctionId()
                                + " ont été remboursés.",
                        "AUCTION_REFUND",
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
