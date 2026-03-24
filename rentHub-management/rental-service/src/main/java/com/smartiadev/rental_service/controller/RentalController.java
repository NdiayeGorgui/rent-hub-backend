package com.smartiadev.rental_service.controller;

import com.smartiadev.rental_service.dto.*;
import com.smartiadev.rental_service.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@Tag(name = "Rentals", description = "Endpoints to manage item rentals")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

    private final RentalService service;

    /* =====================
       CREATE RENTAL
       ===================== */
    @Operation(
            summary = "Create a rental",
            description = "Create a rental request for an item. The authenticated user will be the renter."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid rental request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public RentalResponseDTO create(
            @RequestBody @Valid RentalRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID renterId = UUID.fromString(jwt.getSubject());
        return service.create(dto, renterId);
    }

    /* =====================
       GET MY RENTALS
       ===================== */
    @Operation(
            summary = "Get my rentals",
            description = "Retrieve all rentals requested by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rentals retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public List<RentalResponseDTO> myRentals(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID renterId = UUID.fromString(jwt.getSubject());
        return service.myRentals(renterId);
    }

    /* =====================
      GET RENTALS FOR MY ITEMS (OWNER)
      ===================== */
    @Operation(
            summary = "Get rentals for my items",
            description = "Retrieve all rentals for items owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rentals retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/owner")
    public List<RentalResponseDTO> ownerRentals(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return service.rentalsForMyItems(ownerId);
    }

    /* =====================
       CANCEL RENTAL
       ===================== */
    @Operation(
            summary = "Cancel a rental",
            description = "Cancel a rental. Only the renter can cancel their rental request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the renter"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @DeleteMapping("/{id}")
    public void cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.cancel(id, userId);
    }

    /* =====================
      APPROVE RENTAL (OWNER)
      ===================== */
    @Operation(
            summary = "Approve a rental",
            description = "Approve a rental request for one of the owner's items."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental approved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the owner"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @PatchMapping("/{rentalId}/approve")
    public void approveRental(
            @PathVariable Long rentalId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        service.approve(rentalId, ownerId);
    }


    /* =====================
       GET MY RENTED ITEMS (PAGE)
       ===================== */
    @Operation(
            summary = "Get my rented items",
            description = "Retrieve paginated list of items rented by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rented items retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentedItemDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me/rented")
    public Page<RentedItemDTO> myRentedItems(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID renterId = UUID.fromString(jwt.getSubject());
        return service.myRentedItems(renterId, page, size);
    }

    /* =====================
       GET RENTAL HISTORY FOR USER
       ===================== */
    @Operation(
            summary = "Get rental history for a user",
            description = "Retrieve all items previously rented by a specific user (publicly viewable)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental history retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemSummaryDto.class))),
            @ApiResponse(responseCode = "404", description = "User or rentals not found")
    })
    @GetMapping("/user/{userId}/history")
    public List<ItemSummaryDto> getRentalHistory(
            @PathVariable UUID userId
    ) {
        return service.getRentedItems(userId);
    }

    @GetMapping("/stats/item/{itemId}")
    public ResponseEntity<RentalStatsResponse> getStatsByItem(
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(
                service.getRentalStatsByItem(itemId)
        );
    }

}
