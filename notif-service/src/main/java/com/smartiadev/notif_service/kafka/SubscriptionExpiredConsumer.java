package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionExpiredEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionExpiredConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "subscription.expired",
            groupId = "notification-group"
    )
    public void onExpired(SubscriptionExpiredEvent event) {

        Notification notif = new Notification(
                null,
                event.userId(),
                "⚠️ Votre abonnement premium a expiré",
                "SUBSCRIPTION_EXPIRED",
                false,
                LocalDateTime.now()
        );

        repository.save(notif);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notif
        );

        System.out.println("⚠️ Notification expiration envoyée à : " + event.userId());
    }
}