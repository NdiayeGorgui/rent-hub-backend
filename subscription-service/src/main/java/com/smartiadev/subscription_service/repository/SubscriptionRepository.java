package com.smartiadev.subscription_service.repository;

import com.smartiadev.subscription_service.entity.Subscription;
import com.smartiadev.subscription_service.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserId(UUID userId);


        boolean existsByUserIdAndStatusAndEndDateAfter(
                UUID userId,
                SubscriptionStatus status,
                LocalDateTime now
        );



    List<Subscription> findByStatusAndEndDateBefore(
            SubscriptionStatus status,
            LocalDateTime date
    );

    // stats
    Long countByEndDateAfter(LocalDateTime now);
    Long countByEndDateBefore(LocalDateTime now);
    Long countByAutoRenewTrue();


    List<Subscription> findByEndDateBeforeAndAutoRenewTrue(LocalDateTime date);
}

