package com.smartiadev.auth_service.kafka;

import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.base_domain_service.dto.AuctionPenaltyPaidEvent;
import com.smartiadev.base_domain_service.dto.AuctionPenaltySuspensionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionPenaltyConsumer {

    private final UserRepository userRepository;

    // Suspension suite à pénalité
    @KafkaListener(topics = "auction.penalty.suspension", groupId = "auth-service")
    public void handlePenaltySuspension(AuctionPenaltySuspensionEvent event) {
        userRepository.findById(event.winnerId()).ifPresent(user -> {
            user.setEnabled(false);
            userRepository.save(user);
        });
    }

    // Réactivation après paiement pénalité
    @KafkaListener(topics = "auction.penalty.paid", groupId = "auth-service")
    public void handlePenaltyPaid(AuctionPenaltyPaidEvent event) {
        userRepository.findById(event.winnerId()).ifPresent(user -> {
            user.setEnabled(true);
            userRepository.save(user);
        });
    }
}