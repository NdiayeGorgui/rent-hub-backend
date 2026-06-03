package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.dto.ReviewStatsDto;
import com.smartiadev.auth_service.dto.UserReviewStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "review-service", contextId = "reviewAdminClient")
public interface ReviewClient {

    @GetMapping("/api/reviews/user/{userId}/average")
    Double getAverageRatingForUser(@PathVariable("userId") UUID userId);

    @GetMapping("/api/reviews/user/{userId}/count")
    Long getReviewsCountForUser(@PathVariable UUID userId);

    @GetMapping("/api/admin/reviews/stats/count")
    Long countAllReviews();

    @GetMapping("/api/admin/reviews/stats/average/platform")
    Double getPlatformAverageRating();

    @PostMapping("/api/reviews/internal/users/stats")
    List<UserReviewStatsDto> getUsersStats(
            @RequestBody List<UUID> userIds
    );
    @GetMapping("/api/reviews/internal/users/{userId}/stats")
    UserReviewStatsDto getUserStats(
            @PathVariable UUID userId
    );

    @GetMapping("/api/admin/reviews/stats/internal/stats")
    ReviewStatsDto getStats();


}

