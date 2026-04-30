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

        // ── 1️⃣ Notif pour le propriétaire de l’item ──
        if (!event.itemOwnerId().equals(event.reviewerId())) {

            Notification notifItem = repository.save(
                    new Notification(
                            null,
                            event.itemOwnerId(),
                            "📦 Votre article a reçu un avis (" + event.rating() + "★)",
                            "REVIEW_ITEM",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.itemOwnerId(),
                    notifItem
            );
        }

        // ── 2️⃣ Notif pour user (si USER review) ──
        if (event.reviewedUserId() != null
                && !event.reviewedUserId().equals(event.reviewerId())) {

            Notification notifUser = repository.save(
                    new Notification(
                            null,
                            event.reviewedUserId(),
                            "⭐ Vous avez reçu un avis (" + event.rating() + "★)",
                            "REVIEW_USER",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.reviewedUserId(),
                    notifUser
            );
        }

        System.out.println("✅ Notifications review traitées proprement");
    }
}