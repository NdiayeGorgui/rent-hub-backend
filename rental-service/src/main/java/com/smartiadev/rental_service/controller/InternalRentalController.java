package com.smartiadev.rental_service.controller;

import com.smartiadev.rental_service.dto.RentalResponseDTO;
import com.smartiadev.rental_service.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rentals/internal")
@RequiredArgsConstructor
@Tag(name = "Internal Rentals", description = "Internal endpoints for other microservices. Not exposed to end-users.")
@SecurityRequirement(name = "bearerAuth")
public class InternalRentalController {

    private final RentalService rentalService;

    /* =====================
          GET RENTAL BY ID (INTERNAL)
          ===================== */
    @Operation(
            summary = "Get rental by ID (internal)",
            description = "Retrieve detailed rental information by rental ID. This endpoint is internal and intended for other microservices only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rental not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public RentalResponseDTO getRentalInternal(@PathVariable("id") Long id) {
        return rentalService.getRentalById(id);
    }

    /**
     * Retourne les IDs des items déjà loués sur une période
     */
     /* =====================
       GET UNAVAILABLE ITEMS (INTERNAL)
       ===================== */
    @Operation(
            summary = "Get unavailable items (internal)",
            description = "Return the list of item IDs that are already rented during the specified period. Internal use only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unavailable item IDs retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class, type = "array"))),
            @ApiResponse(responseCode = "400", description = "Invalid date parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/unavailable")
    public List<Long> getUnavailableItems(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return rentalService.findUnavailableItemIds(startDate, endDate);
    }
}
