package com.smartiadev.auction_service.service;

import com.smartiadev.auction_service.client.AuthClient;
import com.smartiadev.auction_service.client.ItemClient;
import com.smartiadev.auction_service.client.PaymentClient;
import com.smartiadev.auction_service.client.SubscriptionClient;
import com.smartiadev.auction_service.dto.*;
import com.smartiadev.auction_service.entity.*;
import com.smartiadev.auction_service.kafka.AuctionEventPublisher;
import com.smartiadev.auction_service.repository.*;
import com.smartiadev.base_domain_service.dto.AuctionBidPlacedEvent;
import com.smartiadev.base_domain_service.dto.AuctionCancellationRequestedEvent;
import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuctionWatcherRepository watcherRepository;
    private final SubscriptionClient subscriptionClient;
    private final ItemClient itemClient;
    private final AuthClient  authClient;
    private final PaymentClient paymentClient;
    private final AuctionEventPublisher eventPublisher;

    public AuctionDto createAuction(CreateAuctionRequest request, UUID userId) {

        // 🔐 PREMIUM
        if (!subscriptionClient.isPremium(userId)) {
            throw new RuntimeException("Premium subscription required");
        }

        // 📦 ITEM
        ItemInternalDTO item = itemClient.getItem(request.itemId());

        if (!item.active()) {
            throw new IllegalStateException("Item is not active");
        }

        if (!item.ownerId().equals(userId)) {
            throw new IllegalStateException("Only owner can create auction");
        }

        if (item.type() != ItemType.AUCTION) {
            throw new IllegalStateException("Item is not configured for auction");
        }

        // 🔴 IMPORTANT : vérifier statut
        if (item.status() != ItemStatus.DRAFT) {
            throw new IllegalStateException("Item must be in DRAFT before creating auction");
        }

        // ❌ Une seule enchère active
        if (auctionRepository.existsByItemIdAndStatus(
                item.id(), AuctionStatus.OPEN)) {
            throw new IllegalStateException("Auction already exists for this item");
        }

        Auction auction = Auction.builder()
                .itemId(item.id())
                .ownerId(userId)
                .startPrice(request.startPrice())
                .currentPrice(request.startPrice())
                .reservePrice(request.reservePrice())
                .views(0)
                .watchers(0)
                .startDate(LocalDateTime.now())
                .endDate(request.endDate())
                .status(AuctionStatus.OPEN)
                .build();

        Auction saved = auctionRepository.save(auction);

        // 🔥 ACTIVER L’ITEM APRÈS CRÉATION DE L’AUCTION
        itemClient.activateItem(item.id(),"true");

        return map(saved);
    }
    @Transactional
    public void placeBid(Long auctionId, PlaceBidRequest request, UUID userId) {

        if (!subscriptionClient.isPremium(userId)) {
            throw new RuntimeException("Premium subscription required");
        }
        var eligibility = authClient.canBid(userId);

        if (!eligibility.canBid()) {
            throw new IllegalStateException("User not allowed to bid");
        }

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // 🚫 Auction fermée
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalStateException("Auction closed");
        }

        // 🚫 Expirée
        if (auction.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Auction expired");
        }

        // 🚫 Owner ne peut pas miser
        if (auction.getOwnerId().equals(userId)) {
            throw new IllegalStateException("Owner cannot bid on own auction");
        }

        // 🚫 Enchère trop basse
        Double current = auction.getCurrentPrice() == null
                ? auction.getStartPrice()
                : auction.getCurrentPrice();

        if (request.amount() <= current) {
            throw new IllegalArgumentException("Bid too low");
        }

        try {
            // 🔥 Update prix (dirty checking automatique)
            auction.setCurrentPrice(request.amount());

            // 🔥 extension automatique si bid dans les 2 dernières minutes
            if (auction.getEndDate().minusMinutes(2).isBefore(LocalDateTime.now())) {
                auction.setEndDate(auction.getEndDate().plusMinutes(2));
            }

            // 🔥 Sauvegarde bid
            bidRepository.save(
                    Bid.builder()
                            .auctionId(auctionId)
                            .bidderId(userId)
                            .amount(request.amount())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

           // 🔥 envoyer event
            eventPublisher.publishBidPlaced(
                    new AuctionBidPlacedEvent(
                            auction.getId(),
                            auction.getItemId(),
                            userId,
                            request.amount()
                    )
            );

        } catch (OptimisticLockingFailureException e) {
            throw new IllegalStateException("Another bid was placed at the same time. Please retry.");
        }
    }

    public List<AuctionDto> getOpenAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.OPEN)
                .stream()
                .map(this::map)
                .toList();
    }

    private AuctionDto map(Auction a) {
        Integer participants = bidRepository.countParticipants(a.getId());

        boolean reserveReached =
                a.getReservePrice() == null ||
                        a.getCurrentPrice() >= a.getReservePrice();

        return new AuctionDto(
                a.getId(),
                a.getItemId(),
                a.getStartPrice(),
                a.getCurrentPrice(),
                participants,
                a.getViews(),
                a.getWatchers(),
                a.getEndDate(),
                a.getStatus().name(),
                reserveReached
        );
    }

    @Transactional
    public AuctionDto getActiveAuctionByItemId(Long itemId) {

        Auction auction = auctionRepository
                .findByItemIdAndStatus(itemId, AuctionStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No active auction found"));

        int views = auction.getViews() == null ? 0 : auction.getViews();

        auction.setViews(views + 1);

        auctionRepository.save(auction);

        return map(auction);
    }

    /*@Transactional
    public AuctionDto watchAuction(Long auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        auction.setWatchers(
                (auction.getWatchers() == null ? 0 : auction.getWatchers()) + 1
        );

        return map(auctionRepository.save(auction));
    }*/

    @Transactional
    public AuctionDto watchAuction(Long auctionId, UUID userId) {
        // 1️⃣ Vérifier si l'utilisateur suit déjà
        if (watcherRepository.existsByAuctionIdAndUserId(auctionId, userId)) {
            throw new RuntimeException("Already watching this auction");
        }

        // 2️⃣ Créer la relation watcher
        watcherRepository.save(
                AuctionWatcher.builder()
                        .auctionId(auctionId)
                        .userId(userId)
                        .build()
        );

        // 3️⃣ Mettre à jour le compteur watchers en fonction de tous les watchers
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        auction.setWatchers((int) watcherRepository.countByAuctionId(auctionId));

        auctionRepository.save(auction);

        // 4️⃣ Retourner DTO
        return map(auction);
    }

    public boolean isWatching(Long auctionId, UUID userId) {

        return watcherRepository.existsByAuctionIdAndUserId(auctionId, userId);

    }

    public AuctionDto getAuctionPublic(Long itemId) {

        Auction auction = auctionRepository
                .findByItemIdAndStatus(itemId, AuctionStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No active auction found"));

        return map(auction);
    }

    public List<UUID> getWatcherIds(Long auctionId){
        return watcherRepository.findByAuctionId(auctionId)
                .stream()
                .map(AuctionWatcher::getUserId)
                .toList();
    }

    @Transactional
    public void closeAuction(Long auctionId){

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        Bid bestBid = bidRepository
                .findTopByAuctionIdOrderByAmountDesc(auctionId)
                .orElse(null);

        boolean reserveMet = true;

        UUID winnerId = null;
        Double winningAmount = null;

        if(bestBid != null){

            if (auction.getReservePrice() != null &&
                    bestBid.getAmount() < auction.getReservePrice()) {

                auction.setStatus(AuctionStatus.RESERVE_NOT_MET);
                reserveMet = false;

            } else {

                auction.setStatus(AuctionStatus.CLOSED);
                winnerId = bestBid.getBidderId();
                winningAmount = bestBid.getAmount();
            }

        } else {

            auction.setStatus(AuctionStatus.CLOSED);
        }

        auctionRepository.save(auction);

        eventPublisher.publishAuctionClosed(
                new AuctionClosedEvent(
                        auction.getId(),
                        auction.getItemId(),
                        auction.getOwnerId(),
                        winnerId,
                        winningAmount,
                        reserveMet
                )
        );
    }

    @Transactional
    public void cancelAuction(Long auctionId) {

        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalStateException("Auction not active");
        }
        log.info("📤 Sending auction.cancellation.requested for auctionId={}, itemId={}, ownerId={}",
                auction.getId(),
                auction.getItemId(),
                auction.getOwnerId()
        );

        // ❌ NE PAS annuler ici
        // 👉 demander paiement

        eventPublisher.publishAuctionCancellationRequested(
                new AuctionCancellationRequestedEvent(
                        auction.getId(),
                        auction.getItemId(),
                        auction.getOwnerId(),
                        50.0
                )
        );
    }

    public void updateAuction(Long auctionId, UUID userId, UpdateAuctionRequest dto) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (!auction.getOwnerId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new RuntimeException("Cannot edit closed auction");
        }

        // 🔥 IMPORTANT
        if (auction.getCurrentPrice() > auction.getStartPrice()) {
            throw new RuntimeException("Cannot edit auction with bids");
        }

        auction.setStartPrice(dto.startPrice());
        auction.setReservePrice(dto.reservePrice());
        auction.setEndDate(dto.endDate());

        auctionRepository.save(auction);
    }
}
