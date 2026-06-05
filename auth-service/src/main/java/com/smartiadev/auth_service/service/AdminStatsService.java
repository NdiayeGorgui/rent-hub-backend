package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.client.*;
import com.smartiadev.auth_service.dto.*;
import com.smartiadev.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
//@Cacheable(value = "adminStats")
public class AdminStatsService {

    private final UserRepository userRepository;
    private final ItemClient itemClient;
    private final RentalClient rentalClient;
    private final ReviewClient reviewClient;
    private final DisputeClient disputeClient;
    private final AuctionClient auctionClient;
    private final SubscriptionClient subscriptionClient;
    private final PaymentClient paymentClient;
    private final ItemPublicClient itemPublicClient;
    private final AuctionPublicClient auctionPublicClient;
    private final ReviewPublicClient reviewPublicClient;

    private final Executor taskExecutor;

    public AdminStatsService(
            UserRepository userRepository,
            ItemClient itemClient,
            RentalClient rentalClient,
            ReviewClient reviewClient,
            DisputeClient disputeClient,
            AuctionClient auctionClient,
            SubscriptionClient subscriptionClient,
            PaymentClient paymentClient,
            ItemPublicClient itemPublicClient,
            AuctionPublicClient auctionPublicClient,
            ReviewPublicClient reviewPublicClient,
            @Qualifier("applicationTaskExecutor") Executor taskExecutor
    ) {
        this.userRepository = userRepository;
        this.itemClient = itemClient;
        this.rentalClient = rentalClient;
        this.reviewClient = reviewClient;
        this.disputeClient = disputeClient;
        this.auctionClient = auctionClient;
        this.subscriptionClient = subscriptionClient;
        this.paymentClient = paymentClient;
        this.itemPublicClient = itemPublicClient;
        this.auctionPublicClient = auctionPublicClient;
        this.reviewPublicClient = reviewPublicClient;
        this.taskExecutor = taskExecutor;
    }


    public AdminStats getStats() {

        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsers();
        Long inactiveUsers = userRepository.countInactiveUsers();
        Long newUsersLast30Days =
                userRepository.countNewUsersLast30Days(
                        LocalDateTime.now().minusDays(30)
                );

        CompletableFuture<ItemStatsDto> itemStatsFuture =
                CompletableFuture.supplyAsync(itemClient::getStats, taskExecutor ).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new ItemStatsDto(0L, 0L,0L,0L);
                });

        CompletableFuture<RentalStatsDto> rentalStatsFuture =
                CompletableFuture.supplyAsync(rentalClient::getStats, taskExecutor).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new RentalStatsDto(0L, 0L,0.0,0L,0L);
                });
        CompletableFuture<ReviewStatsDto> reviewStatsFuture =
                CompletableFuture.supplyAsync(reviewClient::getStats, taskExecutor).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new ReviewStatsDto(0L, 0.0);
                });

        CompletableFuture<DisputeStats> disputeStatsFuture =
                CompletableFuture.supplyAsync(disputeClient::getDisputeStats, taskExecutor).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new DisputeStats(0L, 0L,0L,0L,0L,0L,0.0,0L);
                });

        CompletableFuture<AuctionStats> auctionStatsFuture =
                CompletableFuture.supplyAsync(auctionClient::getAuctionStats, taskExecutor).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new AuctionStats(0L, 0L,0L,0L,0L,0L,0L,0.0);
                });

        CompletableFuture<SubscriptionStats> subscriptionStatsFuture =
                CompletableFuture.supplyAsync(subscriptionClient::getStats, taskExecutor).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new SubscriptionStats(0L, 0L,0L,0L);
                });

        CompletableFuture<PaymentStats> paymentStatsFuture =
                CompletableFuture.supplyAsync(paymentClient::getStats, taskExecutor).exceptionally(ex -> {
                    System.out.println("❌ ItemStats failed: " + ex.getMessage());
                    return new PaymentStats(0L, 0L,0L,0L,0.0,0.0);
                });

        CompletableFuture.allOf(
                itemStatsFuture,
                rentalStatsFuture,
                reviewStatsFuture,
                disputeStatsFuture,
                auctionStatsFuture,
                subscriptionStatsFuture,
                paymentStatsFuture
        ).join();

        ItemStatsDto itemStats = itemStatsFuture.join();
        System.out.println("AUTH RECEIVED DISPUTE = " + itemStats);
        RentalStatsDto rentalStats = rentalStatsFuture.join();
        ReviewStatsDto reviewStats = reviewStatsFuture.join();
        System.out.println("AUTH RECEIVED DISPUTE = " + reviewStats);
        DisputeStats disputeStats = disputeStatsFuture.join();
        System.out.println("AUTH RECEIVED DISPUTE = " + disputeStats);
        AuctionStats auctionStats = auctionStatsFuture.join();
        System.out.println("AUTH RECEIVED DISPUTE = " + auctionStats);
        SubscriptionStats subscriptionStats = subscriptionStatsFuture.join();
        PaymentStats paymentStats = paymentStatsFuture.join();

        return new AdminStats(
                totalUsers,
                activeUsers,
                inactiveUsers,
                newUsersLast30Days,
                itemStats != null ? itemStats : new ItemStatsDto(0L, 0L,0L,0L),
                rentalStats,
                reviewStats != null ? reviewStats : new ReviewStatsDto(0L, 0.0),
                auctionStats,
                subscriptionStats,
                paymentStats,
                disputeStats
        );
    }

    public PublicStatsDto getPublicStats() {

        long totalUsers = userRepository.count();

        long totalItems = 0;
        long totalAuctions = 0;
        double avgRating = 0;

        try {
            totalItems = itemPublicClient.countPublishedItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            totalAuctions = auctionPublicClient.countOpenAuctions();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Double response = reviewPublicClient.getPlatformAverageRating();

            avgRating = response != null ? response : 0.0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PublicStatsDto(
                totalItems,
                totalAuctions,
                totalUsers,
                avgRating
        );
    }
}
