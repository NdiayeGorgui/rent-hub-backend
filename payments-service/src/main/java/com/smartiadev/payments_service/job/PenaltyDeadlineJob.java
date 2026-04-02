package com.smartiadev.payments_service.job;

import com.smartiadev.base_domain_service.dto.AuctionPenaltyEvent;
import com.smartiadev.base_domain_service.dto.AuctionPenaltySuspensionEvent;
import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
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

        if (overdue.isEmpty()) {
            log.info("✅ Aucun penalty expiré trouvé");
            return;
        }

        log.info("⚠️ {} penalty(s) expiré(s) détecté(s)", overdue.size());

        for (Payment p : overdue) {

            // 🔒 Sécurité anti double traitement
            if (p.getStatus() != PaymentStatus.PENDING) {
                continue;
            }

            log.info("⛔ Expiration penalty → userId={}, auctionId={}",
                    p.getUserId(), p.getAuctionId());

            // ✅ Marquer comme expiré
            p.setStatus(PaymentStatus.EXPIRED);

            // 🔥 Suspendre le compte
            kafkaTemplate.send("auction.penalty.suspension",
                    new AuctionPenaltySuspensionEvent(
                            p.getUserId(),
                            p.getAuctionId(),
                            p.getItemId(),
                            p.getAmount()
                    )
            );

            // 🔔 Notification utilisateur
            kafkaTemplate.send("auction.penalty.expired",
                    new AuctionPenaltyEvent(
                            p.getUserId(),
                            p.getAuctionId(),
                            p.getItemId(),
                            p.getAmount(),
                            "Délai dépassé — votre compte a été suspendu"
                    )
            );
        }

        // 💾 Sauvegarde en batch (meilleure perf)
        repository.saveAll(overdue);

        log.info("✅ Traitement des penalties expirés terminé");
    }
}