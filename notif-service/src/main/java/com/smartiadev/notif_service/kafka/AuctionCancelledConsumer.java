package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionCancelledEvent;
import com.smartiadev.base_domain_service.dto.AuctionStrikeEvent;
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
public class AuctionCancelledConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "auction.cancelled",
            groupId = "notification-group"
    )
    public void onAuctionCancelled(AuctionCancelledEvent event) {
        log.info("🔔 Sending notification → userId={}", event.ownerId());
        Notification notification = repository.save(
                new Notification(
                        null,
                        event.ownerId(),
                        "❌ Votre enchère a été annulée (des frais de : " + event.amount() + "$ ont été prélevés dans votre compte)",
                        "AUCTION_CANCELLED",
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