package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionPenaltyEvent;
import com.smartiadev.base_domain_service.dto.AuctionPenaltyPaidEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionPenaltyNotifConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "auction.penalty.pending",
            groupId = "notification-group"
    )
    public void onAuctionPenaltyPending(AuctionPenaltyEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.winnerId(),
                        "⚠️ Votre compte a été suspendu suite au refus de paiement " +
                                "après avoir gagné une enchère. " +
                                "Une pénalité de " + event.amount() + "$ est en attente. " +
                                "Rendez-vous dans 'Mon compte' pour régulariser votre situation.",
                        "AUCTION_PENALTY",
                        false,
                        LocalDateTime.now()
                )
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.winnerId(),
                notification
        );
    }

    @KafkaListener(
            topics = "auction.penalty.paid",
            groupId = "notification-group"
    )
    public void onAuctionPenaltyPaid(AuctionPenaltyPaidEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.winnerId(),
                        "✅ Votre pénalité de " + event.amount() + "$ a été réglée. " +
                                "Votre compte est réactivé, vous pouvez à nouveau participer aux enchères.",
                        "ACCOUNT_REACTIVATED",
                        false,
                        LocalDateTime.now()
                )
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.winnerId(),
                notification
        );
    }
}