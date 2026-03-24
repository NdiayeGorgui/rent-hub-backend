package com.smartiadev.dispute_service.repository;

import com.smartiadev.dispute_service.entity.Dispute;
import com.smartiadev.dispute_service.entity.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    List<Dispute> findByOpenedBy(UUID userId);

    boolean existsByRentalId(Long rentalId);

    long countByStatus(DisputeStatus status);

    long countByCreatedAtAfter(LocalDateTime date);

    // ⏱️ Temps moyen de résolution (en minutes)
    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (d.resolved_at - d.created_at)) / 60)
    FROM dispute_schema.disputes d
    WHERE d.resolved_at IS NOT NULL
""", nativeQuery = true)
    Double avgResolutionTimeMinutes();

    @Query("""
    SELECT COUNT(d)
    FROM Dispute d
    WHERE d.adminDecision IS NOT NULL
""")
    long countWithAdminDecision();


    long countByAdminDecisionIsNotNull();
}

