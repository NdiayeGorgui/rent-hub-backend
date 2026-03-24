package com.smartiadev.messaging_service.kafka;

import com.smartiadev.base_domain_service.dto.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishMessageSent(MessageSentEvent event){

        kafkaTemplate.send(
                "message.sent",
                event
        );

    }

}
