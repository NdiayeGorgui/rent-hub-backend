package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionRenewedEvent;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionRenewedConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket

    @KafkaListener(
            topics = "subscription.renewed",
            groupId = "notification-group"
    )
    public void onSubscriptionRenewed(SubscriptionRenewedEvent event) {

        if (event.paymentType() != PaymentType.SUBSCRIPTION) {
            return;
        }

        // 1️⃣ Créer la notification
        Notification notif = new Notification(
                null,
                event.userId(),
                "🔄 Abonnement renouvelé jusqu’au " + event.newEndDate(),
                "SUBSCRIPTION_RENEWED",
                false,
                LocalDateTime.now()
        );

        // 2️⃣ Sauvegarder en base
        repository.save(notif);

        // 3️⃣ Envoyer via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notif
        );

        System.out.println(
                "🔄 Notification abonnement renouvelé envoyée à l'utilisateur : " + event.userId()
        );
    }
}