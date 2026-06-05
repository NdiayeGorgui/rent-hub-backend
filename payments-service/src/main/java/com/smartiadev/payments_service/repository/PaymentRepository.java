package com.smartiadev.payments_service.repository;

import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.payments_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByUserId(UUID userId);

    //stats
    Long countByStatus(PaymentStatus status);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.status = com.smartiadev.base_domain_service.model.PaymentStatus.SUCCESS
    AND p.type != com.smartiadev.base_domain_service.model.PaymentType.AUCTION_REFUND
""")
    Double sumSuccessfulPayments();

    @Query("""
        SELECT SUM(p.amount)
        FROM Payment p
        WHERE p.status = 'SUCCESS'
    """)
    Double totalRevenue();

    @Query("""
        SELECT SUM(p.amount)
        FROM Payment p
        WHERE p.status = 'SUCCESS'
          AND p.createdAt >= :start
          AND p.createdAt <= :end
    """)
    Double revenueBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    Optional<Payment> findByAuctionIdAndType(Long auctionId, PaymentType type);
    Optional<Payment> findByItemIdAndType(Long itemId, PaymentType type);


    boolean existsByPaymentIntentIdAndType(String paymentIntentId, PaymentType type);

    Optional<Payment> findByUserIdAndTypeAndStatus(
            UUID userId,
            PaymentType type,
            PaymentStatus status
    );

    // PaymentRepository
    Optional<Payment> findByItemIdAndTypeAndStatus(
            Long itemId,
            PaymentType type,
            PaymentStatus status
    );

    List<Payment> findByTypeAndStatusAndPenaltyDeadlineBefore(
            PaymentType type,
            PaymentStatus status,
            LocalDateTime deadline
    );

    @Query("""
    SELECT COALESCE(SUM(
        CASE
            WHEN p.type = com.smartiadev.base_domain_service.model.PaymentType.AUCTION_REFUND
                THEN -p.amount
            ELSE p.amount
        END
    ), 0)
    FROM Payment p
    WHERE p.status = com.smartiadev.base_domain_service.model.PaymentStatus.SUCCESS
""")
    Double calculateNetRevenue();
}

