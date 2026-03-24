package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.AdminStats;
import com.smartiadev.auth_service.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "Admin Statistics", description = "Endpoints for retrieving administrative statistics and system metrics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Operation(
            summary = "Get platform statistics",
            description = "Retrieves aggregated statistics about the platform such as total users, "
                    + "active users, suspended users, and other administrative metrics. "
                    + "Accessible only by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public AdminStats stats() {
        return adminStatsService.getStats();
    }
}
