package com.smartiadev.subscription_service.controller;

import com.smartiadev.subscription_service.dto.SubscriptionStats;
import com.smartiadev.subscription_service.service.SubscriptionStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions/internal")
@RequiredArgsConstructor
@Tag(
        name = "Subscriptions - Internal",
        description = "Internal endpoints for subscription monitoring and statistics"
)
public class SubscriptionStatsController {

    private final SubscriptionStatsService statService;

    /* ============================
       SUBSCRIPTION STATISTICS (INTERNAL)
       ============================ */
    @Operation(
            summary = "Get subscription statistics (internal)",
            description = "Internal endpoint used for monitoring and analytics. Returns aggregated statistics such as total subscriptions, active premium users, and cancelled subscriptions."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Subscription statistics retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscriptionStats.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public SubscriptionStats stats() {
        return statService.getStats();
    }
}