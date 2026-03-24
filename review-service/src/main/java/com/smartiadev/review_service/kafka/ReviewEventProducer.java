package com.smartiadev.review_service.kafka;

import com.smartiadev.base_domain_service.dto.ReviewCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendReviewCreated(ReviewCreatedEvent event) {
        kafkaTemplate.send("review.created", event);
    }
}

