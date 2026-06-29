package com.smartiadev.auction_service.job;


import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import com.smartiadev.auction_service.kafka.AuctionEventPublisher;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.base_domain_service.dto.AuctionExpiredCleanupEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionCleanupJob {

    private final AuctionRepository auctionRepository;
    private final AuctionEventPublisher eventPublisher;

    // Tous les jours à 3h du matin
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredAuctions() {

        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        // Enchères CLOSED, CANCELLED, RESERVE_NOT_MET depuis plus de 7 jours
        List<Auction> toDelete = auctionRepository.findExpiredForCleanup(
                List.of(
                        AuctionStatus.CLOSED,
                        AuctionStatus.CANCELLED,
                        AuctionStatus.RESERVE_NOT_MET
                ),
                cutoff
        );

        log.info("🧹 Nettoyage : {} enchères à supprimer", toDelete.size());

        for (Auction auction : toDelete) {
            // ← Publie event pour que item-service supprime l'item + images
            eventPublisher.publishAuctionExpiredCleanup(
                    new AuctionExpiredCleanupEvent(
                            auction.getId(),
                            auction.getItemId()
                    )
            );
        }

        auctionRepository.deleteAll(toDelete);

        log.info("✅ Nettoyage terminé");
    }
}
