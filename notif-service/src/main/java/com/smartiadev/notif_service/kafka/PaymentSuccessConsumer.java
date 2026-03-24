package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "notification-group"
    )
    public void onPaymentSuccess(PaymentCompletedEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "✅ Paiement réussi de " + event.amount() + " $",
                        "PAYMENT_SUCCESS",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 envoyer au frontend via websocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                notification
        );

        System.out.println(
                "💳 Notification paiement réussi envoyée + websocket : "
                        + event.userId()
        );
    }
}