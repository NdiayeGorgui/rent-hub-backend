package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.DisputeCreatedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DisputeNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "dispute.created", groupId = "notification-group")
    public void onDisputeCreated(DisputeCreatedEvent event) {

        UUID adminId = event.adminId();

        // sécurité
        if (adminId == null) {
            System.out.println("❌ adminId manquant dans event");
            return;
        }

        Notification notification = repository.save(
                new Notification(
                        null,
                        adminId,
                        "⚠️ Nouvelle dispute signalée (raison: " + event.reason() + ")",
                        "DISPUTE",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 WebSocket vers LE BON ADMIN
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + adminId,
                notification
        );

        System.out.println("✅ Notification envoyée à admin " + adminId);
    }
}