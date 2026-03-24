package com.smartiadev.review_service.controller;

import com.smartiadev.review_service.dto.CreateReviewRequest;
import com.smartiadev.review_service.dto.ReviewDto;
import com.smartiadev.review_service.entity.Review;
import com.smartiadev.review_service.repository.ReviewRepository;
import com.smartiadev.review_service.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Endpoints for managing reviews and ratings for items and users")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    /* =====================
      CREATE REVIEW
      ===================== */
    @Operation(
            summary = "Create a new review",
            description = "Authenticated users can create a review for an item or user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID reviewerId = UUID.fromString(jwt.getSubject());
        return reviewService.createReview(request, reviewerId);
    }


    // ⭐ Avis pour un article
    /* =====================
       GET REVIEWS BY ITEM
       ===================== */
    @Operation(
            summary = "Get all reviews for an item",
            description = "Retrieve all reviews associated with a given item ID."
    )
    @GetMapping("/item/{itemId}")
    public List<ReviewDto> getReviewsByItem(@PathVariable Long itemId) {
        return reviewService.getReviewsByItemId(itemId);
    }

    // ⭐ Avis pour un utilisateur
     /* =====================
       GET REVIEWS BY USER
       ===================== */
    @Operation(
            summary = "Get all reviews for a user",
            description = "Retrieve all reviews written for a specific user."
    )
    @GetMapping("/user/{userId}")
    public List<ReviewDto> getReviewsByUser(@PathVariable UUID userId) {
        return reviewService.getReviewsByUser(userId);
    }

    /* =====================
       AVERAGE RATING FOR ITEM
       ===================== */
    @Operation(
            summary = "Get average rating for an item",
            description = "Retrieve the average rating of a specific item based on all reviews."
    )
    @GetMapping("/item/{itemId}/average")
    public Double getAverageRatingForItem(@PathVariable Long itemId) {
        return reviewService.getAverageRatingForItem(itemId);
    }

    /* =====================
      AVERAGE RATING FOR USER
      ===================== */
    @Operation(
            summary = "Get average rating for a user",
            description = "Retrieve the average rating received by a user."
    )
    @GetMapping("/user/{userId}/average")
    public Double getAverageRatingForUser(@PathVariable UUID userId) {
        return reviewService.getAverageRatingForUser(userId);
    }
    /* =====================
          REVIEW COUNT FOR USER
          ===================== */
    @Operation(
            summary = "Get total number of reviews for a user",
            description = "Retrieve the count of reviews written for a specific user."
    )
    @GetMapping("/user/{userId}/count")
    public Long getReviewsCountForUser(@PathVariable UUID userId) {
        return reviewService.getReviewsCountForUser(userId);
    }

    /* =====================
      ITEMS WITH MINIMUM RATING
      ===================== */
    @Operation(
            summary = "Get item IDs with minimum rating",
            description = "Retrieve IDs of items whose average rating meets or exceeds the specified minimum."
    )
    @GetMapping("/items/min-rating")
    public List<Long> getItemsWithMinRating(
            @RequestParam Double minRating
    ) {
        return reviewService.getItemIdsWithMinRating(minRating);
    }

    /* =====================
       ITEMS AVERAGE RATINGS
       ===================== */
    @Operation(
            summary = "Get average ratings for all items",
            description = "Retrieve a map of item IDs to their average ratings."
    )
    @GetMapping("/items/ratings")
    public Map<Long, Double> getItemsRatings() {
        return reviewService.getItemsAverageRatings();
    }

    @GetMapping("/item/{itemId}/count")
    public Long countReviewsForItem(@PathVariable Long itemId) {
        return reviewService.countReviewsForItem(itemId);
    }

    @GetMapping("/rental/{rentalId}/can-review")
    public boolean canReview(
            @PathVariable Long rentalId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        System.out.println("User ID: " + userId);

        return reviewRepository.existsByRentalIdAndReviewerId(rentalId, userId);
    }
}
