package com.smartiadev.review_service.controller;

import com.smartiadev.review_service.service.ReviewStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews/stats")
@RequiredArgsConstructor
@Tag(name = "Review Stats", description = "Admin/internal endpoints to retrieve review statistics and platform ratings")
@SecurityRequirement(name = "bearerAuth")

public class ReviewStatsController {

    private final ReviewStatsService reviewStatsService;

    /* =====================
      COUNT ALL REVIEWS
      ===================== */
    @Operation(
            summary = "Count all reviews",
            description = "Returns the total number of reviews in the system. Admin/internal endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total review count retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public Long countAllReviews() {
        return reviewStatsService.countAllReviews();
    }

    /* =====================
       PLATFORM AVERAGE RATING
       ===================== */
    @Operation(
            summary = "Get platform average rating",
            description = "Returns the average rating across all reviews on the platform. Admin/internal endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Platform average rating retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/average/platform")
    public Double getPlatformAverageRating() {
        return reviewStatsService.getPlatformAverageRating();
    }
}
