package com.smartiadev.auth_service.dto;

public record AdminStats(

        // 👤 UTILISATEURS
        Long totalUsers,
        Long activeUsers,

        // 📦 ARTICLES
        Long totalItems,
        Long publishedItems,

        // 🔁 LOCATIONS
        Long totalRentals,
        Long activeRentals,
        Double totalRevenue,

        // ⭐ AVIS
        Long totalReviews,
        Double averagePlatformRating,

        // ⚖️ DISPUTES
        DisputeStats disputeStats,

        // ⚖️ AUCTIONS
        AuctionStats auctionStats,

        // ⚖️ SUBSCRIPTIONS
        SubscriptionStats subscriptionStats,

        // ⚖️ PAYMENTS
        PaymentStats paymentStats

) {}
