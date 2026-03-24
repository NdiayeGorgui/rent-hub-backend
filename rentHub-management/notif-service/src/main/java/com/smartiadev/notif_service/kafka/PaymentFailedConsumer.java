package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentFailedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "payment.failed",
            groupId = "notification-group"
    )
    public void onPaymentFailed(PaymentFailedEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "❌ Paiement échoué : " + event.reason(),
                        "PAYMENT_FAILED",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 envoyer au frontend en temps réel
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notification
        );

        System.out.println(
                "⚠️ Notification paiement échoué envoyée + websocket : "
                        + event.userId()
        );
    }
}