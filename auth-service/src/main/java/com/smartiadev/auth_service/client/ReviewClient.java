package com.smartiadev.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "review-service")
public interface ReviewClient {

    @GetMapping("/api/reviews/user/{userId}/average")
    Double getAverageRatingForUser(@PathVariable("userId") UUID userId);

    @GetMapping("/api/reviews/user/{userId}/count")
    Long getReviewsCountForUser(@PathVariable UUID userId);

    @GetMapping("/api/admin/reviews/stats/count")
    Long countAllReviews();

    @GetMapping("/api/admin/reviews/stats/average/platform")
    Double getPlatformAverageRating();

}

