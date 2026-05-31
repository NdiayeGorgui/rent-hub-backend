package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.client.ItemClient;
import com.smartiadev.auth_service.client.RentalClient;
import com.smartiadev.auth_service.client.ReviewClient;
import com.smartiadev.auth_service.client.SubscriptionClient;
import com.smartiadev.auth_service.dto.ItemSummaryDto;
import com.smartiadev.auth_service.dto.UserProfileDto;
import com.smartiadev.auth_service.dto.UserProfileInternalDto;
import com.smartiadev.auth_service.dto.UserReviewStatsDto;
import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fallbacks si les services ne répondent pas
        Double rating = 0.0;
        Long count = 0L;
        List<ItemSummaryDto> publishedItems = new ArrayList<>();
        List<ItemSummaryDto> rentedItems = new ArrayList<>();
        boolean isPremium = false;

        try { rating = reviewClient.getAverageRatingForUser(userId); }
        catch (Exception e) { /* log warn */ }

        try { count = reviewClient.getReviewsCountForUser(userId); }
        catch (Exception e) { /* log warn */ }

        try { publishedItems = itemClient.getItemsPublishedByUser(userId); }
        catch (Exception e) { /* log warn */ }

        try { rentedItems = rentalClient.getRentalHistory(userId); }
        catch (Exception e) { /* log warn */ }

        try { isPremium = subscriptionClient.isPremium(userId); }
        catch (Exception e) { /* log warn */ }

        Double safeRating = rating != null ? rating : 0.0;
        Long safeCount = count != null ? count : 0L;
        String badge = computeBadge(safeRating, safeCount);

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

    public List<UserProfileInternalDto> getUsersBatch(
            List<UUID> ids
    ) {

        List<User> users = userRepository.findAllById(ids);

        Map<UUID, UserReviewStatsDto> statsMap =
                reviewClient.getUsersStats(ids)
                        .stream()
                        .collect(Collectors.toMap(
                                UserReviewStatsDto::userId,
                                s -> s
                        ));

        return users.stream()
                .map(user -> {

                    UserReviewStatsDto stats =
                            statsMap.get(user.getId());

                    Double rating =
                            stats != null
                                    ? stats.averageRating()
                                    : 0.0;

                    Long reviews =
                            stats != null
                                    ? stats.reviewsCount()
                                    : 0L;

                    UserProfileInternalDto dto =
                            new UserProfileInternalDto();

                    dto.setUserId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setFullName(user.getFullName());
                    dto.setCity(user.getCity());

                    dto.setAverageRating(rating);
                    dto.setReviewsCount(reviews);

                    dto.setBadge(
                            computeBadge(rating, reviews)
                    );

                    return dto;
                })
                .toList();
    }

    public List<UserProfileInternalDto> getUserBatch(
            List<UUID> ids
    ) {

        return userRepository.findAllById(ids)
                .stream()
                .map(user -> {

                    Double rating = 0.0;
                    Long reviewsCount = 0L;

                    try {
                        rating = reviewClient
                                .getAverageRatingForUser(user.getId());
                    } catch (Exception ignored) {
                    }

                    try {
                        reviewsCount = reviewClient
                                .getReviewsCountForUser(user.getId());
                    } catch (Exception ignored) {
                    }

                    String badge = computeBadge(
                            rating != null ? rating : 0.0,
                            reviewsCount != null ? reviewsCount : 0L
                    );

                    UserProfileInternalDto dto =
                            new UserProfileInternalDto();

                    dto.setUserId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setFullName(user.getFullName());
                    dto.setCity(user.getCity());

                    dto.setAverageRating(
                            rating != null ? rating : 0.0
                    );

                    dto.setReviewsCount(
                            reviewsCount != null ? reviewsCount : 0L
                    );

                    dto.setBadge(badge);

                    return dto;
                })
                .toList();
    }

    private UserProfileInternalDto mapInternal(User user) {

        UserProfileInternalDto dto = new UserProfileInternalDto();

        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setCity(user.getCity());

        // fallback
        dto.setAverageRating(0.0);
        dto.setReviewsCount(0L);
        dto.setBadge("NEW");

        return dto;
    }


}
