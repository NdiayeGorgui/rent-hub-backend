package com.smartiadev.rental_service.controller;

import com.smartiadev.rental_service.service.RentalStatsService;
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
@RequestMapping("/api/admin/rentals/stats")
@RequiredArgsConstructor
@Tag(name = "Rental Stats", description = "Admin/internal endpoints to retrieve rental statistics and revenue")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class RentalStatsController {

    private final RentalStatsService rentalStatsService;

    /* =====================
      COUNT ALL RENTALS
      ===================== */
    @Operation(
            summary = "Count all rentals",
            description = "Returns the total number of rental records in the system. Admin/internal endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total rental count retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count")
    public Long countAllRentals() {
        return rentalStatsService.countAllRentals();
    }

    /* =====================
       COUNT ACTIVE RENTALS
       ===================== */
    @Operation(
            summary = "Count active rentals",
            description = "Returns the number of currently active rentals. Admin/internal endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active rental count retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/count/active")
    public Long countActiveRentals() {
        return rentalStatsService.countActiveRentals();
    }

    /* =====================
       GET TOTAL REVENUE
       ===================== */
    @Operation(
            summary = "Get total rental revenue",
            description = "Returns the total revenue generated from all rentals. Admin/internal endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total revenue retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/revenue")
    public Double getTotalRevenue() {
        return rentalStatsService.getTotalRevenue();
    }
}
