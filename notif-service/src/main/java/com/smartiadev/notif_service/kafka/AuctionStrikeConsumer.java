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

        if (event.restricted()) {

            message = "⛔ Votre compte a été suspendu des enchères (3 manquements).";

        } else {

            message = "⚠️ Vous avez reçu un avertissement car vous n'avez pas respecté un engagement lors d'une enchère.. "
                    + "Strikes : " + event.strikes() + "/3"
            +"Après 3 avertissements, la participation aux enchères sera suspendue.";
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

        // Envoi WebSocket en temps réel
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notification
        );

        System.out.println(
                "⚠️ Notification strike envoyée à : " + event.userId()
        );
    }
}