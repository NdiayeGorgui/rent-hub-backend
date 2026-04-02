package com.smartiadev.payments_service.entity;

import com.smartiadev.base_domain_service.model.PaymentStatus;
import com.smartiadev.base_domain_service.model.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", schema = "payment_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;
    private Long  itemId;;
    private Long auctionId;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // SUCCESS, FAILED, PENDING

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    // 🔥 STRIPE
    @Column(unique = true)
    private String paymentIntentId;

    private String failureReason;

    private LocalDateTime createdAt;
    private LocalDateTime penaltyDeadline; // deadline pour payer avant suspension
}

