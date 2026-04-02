package com.smartiadev.payments_service.job;

import com.smartiadev.base_domain_service.dto.AuctionPenaltyEvent;
import com.smartiadev.base_domain_service.dto.AuctionPenaltySuspensionEvent;
import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PenaltyDeadlineJob {

    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRate = 3600000) // toutes les heures
    @Transactional
    public void suspendOverdueWinners() {

        List<Payment> overdue = repository
                .findByTypeAndStatusAndPenaltyDeadlineBefore(
                        PaymentType.AUCTION_PENALTY,
                        PaymentStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Payment p : overdue) {
            // Marque comme expiré
            p.setStatus(PaymentStatus.EXPIRED);
            repository.save(p);

            // Suspend maintenant
            kafkaTemplate.send("auction.penalty.suspension",
                    new AuctionPenaltySuspensionEvent(
                            p.getUserId(),
                            p.getAuctionId(),
                            p.getItemId(),
                            p.getAmount()
                    )
            );

            // Notifie
            kafkaTemplate.send("auction.penalty.expired",
                    new AuctionPenaltyEvent(
                            p.getUserId(), p.getAuctionId(), p.getItemId(), p.getAmount(),
                            "Délai dépassé — votre compte a été suspendu"
                    )
            );
        }
    }
}
