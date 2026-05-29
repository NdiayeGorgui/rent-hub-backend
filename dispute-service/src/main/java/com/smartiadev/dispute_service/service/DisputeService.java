package com.smartiadev.dispute_service.service;

import com.smartiadev.base_domain_service.dto.DisputeCreatedEvent;
import com.smartiadev.base_domain_service.dto.ItemDeactivatedEvent;
import com.smartiadev.base_domain_service.dto.UserSuspendedEvent;
import com.smartiadev.dispute_service.client.*;
import com.smartiadev.dispute_service.dto.*;
import com.smartiadev.dispute_service.entity.Dispute;
import com.smartiadev.dispute_service.entity.DisputeStatus;
import com.smartiadev.dispute_service.kafka.DisputeEventProducer;
import com.smartiadev.dispute_service.repository.DisputeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository repository;
    private final RentalClient rentalClient;
    private final ItemClient itemClient;
    private final AuthClient authClient;
    private final DisputeEventProducer eventProducer;
    private final PaymentClient paymentClient;
    private final AuctionClient  auctionClient;


    @Transactional
    public DisputeDto create(CreateDisputeRequest request, UUID userId) {

        // ── CAS LOCATION ──────────────────────────────────────
        if (request.rentalId() != null) {

            var rental = rentalClient.getRental(request.rentalId());

            if (!"ENDED".equals(rental.status())) {
                throw new IllegalStateException("Rental not ended");
            }

            if (!userId.equals(rental.ownerId())
                    && !userId.equals(rental.renterId())) {
                throw new IllegalStateException("Forbidden");
            }

            if (repository.existsByRentalId(request.rentalId())) {
                throw new IllegalStateException("Dispute already exists for this rental");
            }

            UUID reported = userId.equals(rental.ownerId())
                    ? rental.renterId()
                    : rental.ownerId();

            Dispute dispute = Dispute.builder()
                    .rentalId(rental.id())
                    .itemId(rental.itemId())
                    .openedBy(userId)
                    .reportedUserId(reported)
                    .reason(request.reason())
                    .description(request.description())
                    .status(DisputeStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();

            return saveAndNotify(dispute);
        }

        // ── CAS ENCHÈRE ───────────────────────────────────────
        if (request.auctionId() != null) {

            if (request.reportedUserId() == null) {
                throw new IllegalArgumentException(
                        "reportedUserId obligatoire pour un litige d'enchère"
                );
            }

            // ✅ Chaque utilisateur ne peut ouvrir qu'un seul litige par enchère
            if (repository.existsByAuctionIdAndOpenedBy(request.auctionId(), userId)) {
                throw new IllegalStateException(
                        "Vous avez déjà ouvert un litige pour cette enchère"
                );
            }

            // Récupère l'itemId depuis auction-service
            var auction = auctionClient.getAuction(request.auctionId());

            Dispute dispute = Dispute.builder()
                    .auctionId(request.auctionId())
                    .itemId(auction.itemId())
                    .openedBy(userId)
                    .reportedUserId(request.reportedUserId())
                    .reason(request.reason())
                    .description(request.description())
                    .status(DisputeStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();

            return saveAndNotify(dispute);
        }

        throw new IllegalArgumentException("rentalId ou auctionId est obligatoire");
    }

    // ── Méthode commune save + kafka ──────────────────────────
    private DisputeDto saveAndNotify(Dispute dispute) {

        Dispute saved = repository.save(dispute);

        var admins = authClient.getAdmins();

        if (admins == null || admins.isEmpty()) {
            throw new IllegalStateException("No admin available");
        }

        UUID adminId = admins.get(0).id();

        eventProducer.disputeCreated(
                new DisputeCreatedEvent(
                        saved.getId(),
                        saved.getRentalId(),
                        saved.getAuctionId(),
                        saved.getItemId(),
                        adminId,
                        saved.getOpenedBy(),
                        saved.getReportedUserId(),
                        saved.getReason()
                )
        );

// ── BATCH DATA ─────────────────────────────

        List<ItemInternalDTO> items =
                itemClient.getItemsBatch(
                        List.of(saved.getItemId())
                );

        List<UUID> userIds = new ArrayList<>();

        userIds.add(saved.getOpenedBy());

        if (saved.getReportedUserId() != null) {
            userIds.add(saved.getReportedUserId());
        }

        List<UserResponse> users =
                authClient.getUsersBatch(userIds);

        Map<Long, ItemInternalDTO> itemMap =
                items.stream()
                        .collect(Collectors.toMap(
                                ItemInternalDTO::id,
                                i -> i
                        ));

        Map<UUID, UserResponse> userMap =
                users.stream()
                        .collect(Collectors.toMap(
                                UserResponse::id,
                                u -> u
                        ));

        return map(saved, itemMap, userMap);

    }

    public List myDisputes(UUID userId) {

        List<Dispute> disputes =
                repository.findByOpenedBy(userId);

        List<Long> itemIds = disputes.stream()
                .map(Dispute::getItemId)
                .distinct()
                .toList();

        List<UUID> userIds = disputes.stream()
                .flatMap(d -> java.util.stream.Stream.of(
                        d.getOpenedBy(),
                        d.getReportedUserId()
                ))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        List<ItemInternalDTO> items =
                itemClient.getItemsBatch(itemIds);

        List<UserResponse> users =
                authClient.getUsersBatch(userIds);

        Map<Long, ItemInternalDTO> itemMap =
                items.stream()
                        .collect(Collectors.toMap(
                                ItemInternalDTO::id,
                                i -> i
                        ));

        Map<UUID, UserResponse> userMap =
                users.stream()
                        .collect(Collectors.toMap(
                                UserResponse::id,
                                u -> u
                        ));

        return disputes.stream()
                .map(d -> map(d, itemMap, userMap))
                .toList();

    }

    public List all() {

        List<Dispute> disputes = repository.findAll();

        List<Long> itemIds = disputes.stream()
                .map(Dispute::getItemId)
                .distinct()
                .toList();

        List<UUID> userIds = disputes.stream()
                .flatMap(d -> java.util.stream.Stream.of(
                        d.getOpenedBy(),
                        d.getReportedUserId()
                ))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        List<ItemInternalDTO> items =
                itemClient.getItemsBatch(itemIds);

        List<UserResponse> users =
                authClient.getUsersBatch(userIds);

        Map<Long, ItemInternalDTO> itemMap =
                items.stream()
                        .collect(Collectors.toMap(
                                ItemInternalDTO::id,
                                i -> i
                        ));

        Map<UUID, UserResponse> userMap =
                users.stream()
                        .collect(Collectors.toMap(
                                UserResponse::id,
                                u -> u
                        ));

        return disputes.stream()
                .map(d -> map(d, itemMap, userMap))
                .toList();

    }

    @Transactional
    public void resolve(Long id, ResolveDisputeRequest request, UUID adminId) {

        Dispute dispute = repository.findById(id)
                .orElseThrow();

        dispute.setStatus(DisputeStatus.valueOf(request.decision()));
        dispute.setAdminDecision(request.adminDecision());
        dispute.setResolvedAt(LocalDateTime.now());

        if ("DEACTIVATE_ITEM".equals(request.action())) {
            itemClient.deactivate(dispute.getItemId());
            eventProducer.itemDeactivated(
                    new ItemDeactivatedEvent(
                            dispute.getItemId(),
                            dispute.getId(),
                            adminId,
                            request.decision(),
                            LocalDateTime.now()
                    )
            );
        }

        if ("SUSPEND_USER".equals(request.action())) {
            authClient.suspend(dispute.getReportedUserId());
            eventProducer.userSuspended(
                    new UserSuspendedEvent(
                            dispute.getReportedUserId(),
                            dispute.getId(),
                            adminId,
                            request.decision(),
                            LocalDateTime.now()
                    )
            );
        }

        if ("REFUND_AUCTION_FEE".equals(request.action())
                && DisputeStatus.RESOLVED.name().equals(request.decision())) {

            if (dispute.getReportedUserId() == null) {
                throw new IllegalStateException("Aucun utilisateur signalé sur ce litige");
            }

            // ✅ paymentIntentId récupéré automatiquement via Feign
            PaymentResponse payment = paymentClient.getAuctionFeeByItemId(dispute.getItemId());

            // ✅ winnerId = reportedUserId (le gagnant qui refuse de payer)
            paymentClient.refundAuctionFee(
                    payment.paymentIntentId(),
                    dispute.getReportedUserId(),
                    dispute.getId()
            );
        }

        repository.save(dispute);
    }

    private DisputeDto map(
            Dispute d,
            Map<Long, ItemInternalDTO> itemMap,
            Map<UUID, UserResponse> userMap
    ) {

        var item = itemMap.get(d.getItemId());

        var openedUser = userMap.get(d.getOpenedBy());

        var reportedUser = d.getReportedUserId() != null
                ? userMap.get(d.getReportedUserId())
                : null;

        return new DisputeDto(
                d.getId(),
                d.getRentalId(),
                d.getAuctionId(),
                d.getItemId(),

                d.getOpenedBy(),
                openedUser != null
                        ? openedUser.username()
                        : null,

                d.getReportedUserId(),
                reportedUser != null
                        ? reportedUser.username()
                        : null,

                item != null
                        ? item.title()
                        : null,

                d.getReason(),
                d.getStatus().name(),
                d.getAdminDecision()
        );
    }
}

