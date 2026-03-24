package com.smartiadev.subscription_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions", schema = "subscription_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancelledAt;
    private Boolean autoRenew;

    // référence paiement (future)
    private Long paymentId;
    private String paymentRef;
    // ⭐ méthode utile
    public boolean isPremiumActive() {

        return (status == SubscriptionStatus.ACTIVE
                || status == SubscriptionStatus.GRACE_PERIOD)
                && endDate.isAfter(LocalDateTime.now());
    }

}

