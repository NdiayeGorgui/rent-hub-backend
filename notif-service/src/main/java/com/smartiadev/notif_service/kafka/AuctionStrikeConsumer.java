package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionStrikeEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionStrikeConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "auction.strike",
            groupId = "notification-group"
    )
    public void onAuctionStrike(AuctionStrikeEvent event) {

        String message;

        int strikes = event.strikes();

        if (strikes == 1) {
            message = "⚠️ Vous avez reçu un avertissement suite à un non-respect d'engagement lors d'une enchère.";
        } else if (strikes == 2) {
            message = "⚠️ Vous avez reçu un deuxième avertissement. Attention, un troisième entraînera une suspension des enchères.";
        } else if (strikes >= 3) {
            message = "⛔ Votre compte a été suspendu des enchères (3 manquements).";
        } else {
            message = "⚠️ Avertissement enchère.";
        }

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.userId(),
                        message,
                        "AUCTION_STRIKE",
                        false,
                        LocalDateTime.now()
                )
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notification
        );

        System.out.println("⚠️ Notification strike envoyée à : " + event.userId());
    }
}