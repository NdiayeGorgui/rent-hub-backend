package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.client.ItemClient;
import com.smartiadev.auth_service.client.RentalClient;
import com.smartiadev.auth_service.client.ReviewClient;
import com.smartiadev.auth_service.client.SubscriptionClient;
import com.smartiadev.auth_service.dto.ItemSummaryDto;
import com.smartiadev.auth_service.dto.UserProfileDto;
import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ReviewClient reviewClient;
    private final ItemClient itemClient;
    private final RentalClient rentalClient;
    private final SubscriptionClient subscriptionClient;

    /**
     * 👤 PROFIL PUBLIC
     */
   // @Cacheable(value = "user-profile", key = "#userId")
    //@CacheEvict(value = "user-profile", allEntries = true)
    public UserProfileDto getPublicProfile(UUID userId) {

        // 1️⃣ USER (auth-service DB)
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2️⃣ RÉPUTATION
        Double rating =
                reviewClient.getAverageRatingForUser(userId);
        Long count =
                reviewClient.getReviewsCountForUser(userId);

        Double safeRating = rating != null ? rating : 0.0;
        Long safeCount = count != null ? count : 0L;

        String badge = computeBadge(safeRating, safeCount);

        // 3️⃣ HISTORIQUE
        List<ItemSummaryDto> publishedItems =
                itemClient.getItemsPublishedByUser(userId);

        List<ItemSummaryDto> rentedItems =
                rentalClient.getRentalHistory(userId);

        boolean isPremium =
                subscriptionClient.isPremium(userId);


        return UserProfileDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .city(user.getCity())
                .premium(isPremium)
                .averageRating(safeRating)
                .reviewsCount(safeCount)
                .badge(badge)
                .publishedItems(publishedItems)
                .rentedItems(rentedItems)
                .roles(new ArrayList<>(user.getRoles()))
                .build();
    }

    /**
     * 🔐 PROFIL PRIVÉ (même base pour l’instant)
     */
    public UserProfileDto getMyProfile(UUID userId) {
        return getPublicProfile(userId);
    }

    // ⭐ BADGE
    private String computeBadge(Double rating, Long count) {

        if (count < 3) return "NEW";
        if (rating >= 4.8) return "EXCELLENT";
        if (rating >= 4.0) return "VERY_GOOD";
        if (rating >= 3.0) return "GOOD";
        return "AVERAGE";
    }
}
