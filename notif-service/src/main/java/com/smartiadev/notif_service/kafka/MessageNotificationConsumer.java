package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.MessageSentEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MessageNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "message.sent")
    public void onMessageSent(MessageSentEvent event){

        Notification notification = repository.save(
                new Notification(
                        null,
                        event.receiverId(),
                        "📩 Nouveau message reçu",
                        "MESSAGE",
                        false,
                        LocalDateTime.now()
                )
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.receiverId(),
                notification
        );
    }
}
