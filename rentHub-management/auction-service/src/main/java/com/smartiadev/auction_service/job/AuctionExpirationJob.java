package com.smartiadev.auction_service.job;

import com.smartiadev.auction_service.kafka.AuctionEventPublisher;
import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.auction_service.entity.*;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.auction_service.repository.BidRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionExpirationJob {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuctionEventPublisher eventPublisher;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void closeExpiredAuctions() {

        var expiredAuctions =
                auctionRepository.findByStatusAndEndDateBefore(
                        AuctionStatus.OPEN,
                        LocalDateTime.now()
                );

        for (var auction : expiredAuctions) {

            bidRepository.findTopByAuctionIdOrderByAmountDesc(auction.getId())
                    .ifPresentOrElse(
                            bid -> {
                                // 🔒 Vérifier si le prix de réserve est atteint
                                boolean reserveMet = auction.getReservePrice() == null
                                        || bid.getAmount() >= auction.getReservePrice();

                                if (reserveMet) {
                                    auction.setStatus(AuctionStatus.CLOSED); // Gagnant
                                    eventPublisher.publishAuctionClosed(
                                            new AuctionClosedEvent(
                                                    auction.getId(),
                                                    auction.getItemId(),
                                                    auction.getOwnerId(),
                                                    bid.getBidderId(),
                                                    bid.getAmount(),
                                                    true // reserveMet
                                            )
                                    );
                                } else {
                                    auction.setStatus(AuctionStatus.RESERVE_NOT_MET);
                                    eventPublisher.publishAuctionClosed(
                                            new AuctionClosedEvent(
                                                    auction.getId(),
                                                    auction.getItemId(),
                                                    auction.getOwnerId(),
                                                    null,
                                                    bid.getAmount(),
                                                    false // reserveMet
                                            )
                                    );
                                }
                            },
                            () -> {
                                // ❌ Aucune enchère
                                auction.setStatus(AuctionStatus.CLOSED);
                                eventPublisher.publishAuctionClosed(
                                        new AuctionClosedEvent(
                                                auction.getId(),
                                                auction.getItemId(),
                                                auction.getOwnerId(),
                                                null,
                                                null,
                                                false // reserveMet
                                        )
                                );
                            }
                    );
        }

        auctionRepository.saveAll(expiredAuctions);
    }
}