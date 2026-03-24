package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentCreatedEvent;
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
public class PaymentCreatedConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;


    @KafkaListener(
            topics = "payment.created",
            groupId = "notification-group"
    )
    public void onPaymentCreated(PaymentCreatedEvent event) {
        UUID adminId = event.adminId();

        // notification pour l'utilisateur
        Notification userNotification = repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "✅ Paiement envoyé : " + event.amount() + "$",
                        "PAYMENT_CREATED",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 envoyer au frontend via websocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                userNotification
        );

        // notification pour l'admin
        Notification adminNotification = repository.save(
                new Notification(
                        null,
                        adminId,
                        "💰 Nouveau paiement envoyé par "
                                + event.fullName()
                                + " : " + event.amount() + "$",
                        "ADMIN_PAYMENT_CREATED",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 websocket pour l'admin
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + adminId,
                adminNotification
        );

        System.out.println(
                "💰 Notification paiement envoyée + websocket"
        );
    }
}