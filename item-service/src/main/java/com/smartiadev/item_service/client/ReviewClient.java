package com.smartiadev.item_service.client;

import com.smartiadev.item_service.dto.OwnerReviewStatsDto;
import com.smartiadev.item_service.dto.ReviewDto;
import com.smartiadev.item_service.dto.UserReviewStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "review-service")
public interface ReviewClient {

    @GetMapping("/api/reviews/item/{itemId}/average")
    Double getAverageRatingForItem(@PathVariable("itemId") Long itemId);

    @GetMapping("/api/reviews/item/{itemId}")
    List<ReviewDto> getReviews(@PathVariable Long itemId);

    @GetMapping("/api/reviews/items/min-rating")
    List<Long> getItemIdsWithMinRating(
            @RequestParam Double minRating
    );

    @GetMapping("/api/reviews/items/ratings")
    Map<Long, Double> getItemsRatings();

    @PostMapping("/internal/owners/stats")
    List<OwnerReviewStatsDto> getOwnersStats(
            @RequestBody List<UUID> ownerIds
    );

}
