package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.service.ItemStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/items/stats")
@RequiredArgsConstructor
@Tag(name = "Admin Item Stats", description = "Administrative endpoints for item statistics")
@SecurityRequirement(name = "bearerAuth")

public class ItemStatsController {

    private final ItemStatsService itemStatsService;

    /* =====================
        COUNT ALL ITEMS
        ===================== */
    @Operation(
            summary = "Internal: Count all items",
            description = "Retrieve the total number of items in the system. Internal use only by other microservices."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - microservice role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public Long countAllItems() {
        return itemStatsService.countAllItems();
    }

    /* =====================
       COUNT PUBLISHED ITEMS
       ===================== */
    @Operation(
            summary = "Internal: Count published items",
            description = "Retrieve the total number of items that are currently published and active. Internal use only by other microservices."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - microservice role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count/published")
    public Long countPublishedItems() {
        return itemStatsService.countPublishedItems();
    }
}
