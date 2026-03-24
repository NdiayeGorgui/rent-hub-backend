package com.smartiadev.dispute_service.service;

import com.smartiadev.base_domain_service.dto.DisputeCreatedEvent;
import com.smartiadev.base_domain_service.dto.ItemDeactivatedEvent;
import com.smartiadev.base_domain_service.dto.UserSuspendedEvent;
import com.smartiadev.dispute_service.client.AuthClient;
import com.smartiadev.dispute_service.client.ItemClient;
import com.smartiadev.dispute_service.client.RentalClient;
import com.smartiadev.dispute_service.dto.CreateDisputeRequest;
import com.smartiadev.dispute_service.dto.DisputeDto;
import com.smartiadev.dispute_service.dto.ResolveDisputeRequest;
import com.smartiadev.dispute_service.entity.Dispute;
import com.smartiadev.dispute_service.entity.DisputeStatus;
import com.smartiadev.dispute_service.kafka.DisputeEventProducer;
import com.smartiadev.dispute_service.repository.DisputeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository repository;
    private final RentalClient rentalClient;
    private final ItemClient itemClient;
    private final AuthClient authClient;
    private final DisputeEventProducer eventProducer;


    @Transactional
    public DisputeDto create(CreateDisputeRequest request, UUID userId) {

        var rental = rentalClient.getRental(request.rentalId());

        if (!"ENDED".equals(rental.status())) {
            throw new IllegalStateException("Rental not ended");
        }

        if (!userId.equals(rental.ownerId())
                && !userId.equals(rental.renterId())) {
            throw new IllegalStateException("Forbidden");
        }

        if (repository.existsByRentalId(request.rentalId())) {
            throw new IllegalStateException("Dispute already exists");
        }

        UUID reported =
                userId.equals(rental.ownerId())
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

        // sauvegarde
        Dispute saved = repository.save(dispute);

        var admins = authClient.getAdmins();

        if (admins == null || admins.isEmpty()) {
            throw new IllegalStateException("No admin available");
        }

        UUID adminId = admins.get(0).id();

        // 🔔 publication de l'évènement Kafka
        eventProducer.disputeCreated(
                new DisputeCreatedEvent(
                        saved.getId(),
                        saved.getRentalId(),
                        saved.getItemId(),
                        adminId,
                        saved.getOpenedBy(),
                        saved.getReportedUserId(),
                        saved.getReason()
                )
        );

        return map(saved);
    }

    public List<DisputeDto> myDisputes(UUID userId) {
        return repository.findByOpenedBy(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<DisputeDto> all() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public void resolve(Long id, ResolveDisputeRequest request, UUID adminId) {

        Dispute dispute = repository.findById(id)
                .orElseThrow();

        // statut
        dispute.setStatus(DisputeStatus.valueOf(request.decision()));

        // commentaire admin
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

        repository.save(dispute);
    }


    private DisputeDto map(Dispute d) {
        return new DisputeDto(
                d.getId(),
                d.getRentalId(),
                d.getItemId(),
                d.getOpenedBy(),
                d.getReportedUserId(),
                d.getReason(),
                d.getStatus().name(),
                d.getAdminDecision()
        );
    }
}

