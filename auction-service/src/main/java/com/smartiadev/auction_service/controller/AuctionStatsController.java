package com.smartiadev.auction_service.controller;

import com.smartiadev.auction_service.dto.AuctionStats;
import com.smartiadev.auction_service.service.AuctionStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions/internal")
@RequiredArgsConstructor
@Tag(
        name = "Auctions - Internal",
        description = "Internal endpoints for auction monitoring and statistics"
)
public class AuctionStatsController {

    private final AuctionStatsService statsService;

    /* ============================
       AUCTION STATISTICS (INTERNAL)
       ============================ */
    @Operation(
            summary = "Get auction statistics (internal)",
            description = "Internal endpoint used for monitoring and analytics. Returns aggregated auction statistics such as total auctions, active auctions, closed auctions, and total bids."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Auction statistics retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuctionStats.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public AuctionStats stats() {
        return statsService.getStats();
    }
}