package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionGracePeriodStartedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionGracePeriodConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "subscription.grace.started",
            groupId = "notification-group"
    )
    public void onGracePeriodStarted(SubscriptionGracePeriodStartedEvent event) {

        Notification notif = new Notification(
                null,
                event.userId(),
                "⚠️ Payment failed. You have until " + event.graceEndDate() + " to update your payment method.",
                "SUBSCRIPTION_GRACE_PERIOD",
                false,
                LocalDateTime.now()
        );

        // sauvegarde en base
        repository.save(notif);

        // envoi websocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notif
        );

        System.out.println(
                "⚠️ Notification grace period envoyée à : " + event.userId()
        );
    }
}