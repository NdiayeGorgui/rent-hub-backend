package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.*;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class RentalNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    private void sendNotification(Notification notification) {
        Notification saved = repository.save(notification);

        // 📡 envoyer au frontend via websocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + saved.getUserId(),
                saved
        );
    }

    @KafkaListener(topics = "rental.approved")
    public void approved(RentalApprovedEvent event) {

        sendNotification(new Notification(
                null,
                event.renterId(),
                "Votre demande de location est approuvée ✅",
                "APPROVED",
                false,
                LocalDateTime.now()
        ));

        System.out.println("📩 Notification : location approuvée");
    }

    @KafkaListener(topics = "rental.started")
    public void started(RentalStartedEvent event) {

        sendNotification(new Notification(
                null,
                event.renterId(),
                "Votre location a commencé 🚀",
                "STARTED",
                false,
                LocalDateTime.now()
        ));

        System.out.println("🚀 Location commencée");
    }

    @KafkaListener(topics = "rental.cancelled")
    public void cancelled(RentalCancelledEvent event) {

        sendNotification(new Notification(
                null,
                event.renterId(),
                "Le produit est déjà loué ❌",
                "CANCELLED",
                false,
                LocalDateTime.now()
        ));

        System.out.println("❌ Produit déjà loué");
    }

    @KafkaListener(topics = "rental.cancelled.by.user")
    public void cancelledByUser(RentalCancelledByUserEvent event) {

        sendNotification(new Notification(
                null,
                event.renterId(),
                "Votre location a été annulée ❌",
                "CANCELLED",
                false,
                LocalDateTime.now()
        ));

        System.out.println("❌ Location annulée par utilisateur");
    }

    @KafkaListener(topics = "rental.ended")
    public void ended(RentalEndedEvent event) {

        sendNotification(new Notification(
                null,
                event.getRenterId(),
                "Votre location est terminée 📦",
                "ENDED",
                false,
                LocalDateTime.now()
        ));

        System.out.println("📦 Location terminée");
    }

    @KafkaListener(
            topics = "rental.request.expired",
            groupId = "notif-service-group"
    )
    public void requestExpired(RentalRequestExpiredEvent event) {

        sendNotification(new Notification(
                null,
                event.getRenterId(),
                "Votre demande de location a expiré car elle n'a pas été approuvée ⏳",
                "EXPIRED",
                false,
                LocalDateTime.now()
        ));

        System.out.println("⏳ Demande de location expirée");
    }
}