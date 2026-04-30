package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.ReviewCreatedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReviewNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "review.created")
    public void onReviewCreated(ReviewCreatedEvent event) {

        // ── 1️⃣ NOTIF AU PROPRIÉTAIRE DE L’ITEM (TOUJOURS) ──
        if (event.itemOwnerId() != null) {

            Notification itemNotif = repository.save(
                    new Notification(
                            null,
                            event.itemOwnerId(),
                            "⭐ Votre article a reçu un avis (" + event.rating() + "★)",
                            "REVIEW",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.itemOwnerId(),
                    itemNotif
            );

            System.out.println("📦 Notif envoyée au owner : " + event.itemOwnerId());
        }

        // ── 2️⃣ NOTIF À L’UTILISATEUR NOTÉ (SEULEMENT USER REVIEW) ──
        if (event.reviewedUserId() != null) {

            Notification userNotif = repository.save(
                    new Notification(
                            null,
                            event.reviewedUserId(),
                            "⭐ Vous avez reçu un nouvel avis (" + event.rating() + "★)",
                            "REVIEW",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.reviewedUserId(),
                    userNotif
            );

            System.out.println("👤 Notif envoyée à l'utilisateur : " + event.reviewedUserId());
        }
    }
}