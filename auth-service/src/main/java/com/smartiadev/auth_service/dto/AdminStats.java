package com.smartiadev.auth_service.dto;

public record AdminStats(

        // 👤 USERS (reste simple)
        Long totalUsers,
        Long activeUsers,
        Long iactiveUsers,
        Long newUsers,

        // 📦 STATS MICROSERVICES
        ItemStatsDto itemStats,
        RentalStatsDto rentalStats,
        ReviewStatsDto reviewStats,
        AuctionStats auctionStats,
        SubscriptionStats subscriptionStats,
        PaymentStats paymentStats,
        DisputeStats disputeStats
) {}
