package com.smartiadev.dispute_service.controller;

import com.smartiadev.dispute_service.dto.DisputeStats;
import com.smartiadev.dispute_service.service.DisputeStatsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/disputes/stats")
@RequiredArgsConstructor
@Tag(name = "Dispute Statistics (Internal)", description = "Internal endpoint used by other microservices (e.g., Auth Service) to retrieve aggregated dispute metrics. Not intended for public use.")
@SecurityRequirement(name = "bearerAuth")

public class DisputeStatsController {

    private final DisputeStatsService service;

    @Operation(
            summary = "Get dispute statistics (internal use)",
            description = "Returns aggregated dispute statistics such as total disputes, open disputes, and resolved disputes. "
                    + "This endpoint is intended for internal microservice communication (e.g., used by Auth Service) and is restricted to administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispute statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public DisputeStats stats() {
        return service.getStats();
    }
}