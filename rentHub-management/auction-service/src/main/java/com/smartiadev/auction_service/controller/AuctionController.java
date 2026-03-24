package com.smartiadev.auction_service.controller;

import com.smartiadev.auction_service.dto.*;
import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Endpoints for managing auctions and bids")
public class AuctionController {

    private final AuctionService service;


    /* ============================
       CREATE AUCTION
       ============================ */
    @Operation(
            summary = "Create auction",
            description = "Create a new auction for an item. Authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public AuctionDto create(
            @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.createAuction(request, userId);
    }

    /* ============================
       PLACE BID
       ============================ */
    @Operation(
            summary = "Place a bid",
            description = "Place a bid on an existing auction. Authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bid placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid bid amount"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Auction not found"),
            @ApiResponse(responseCode = "409", description = "Bid too low or auction closed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/bid")
    public void bid(
            @PathVariable Long id,
            @RequestBody PlaceBidRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.placeBid(id, request, userId);
    }

    /* ============================
       LIST OPEN AUCTIONS (PUBLIC)
       ============================ */
    @Operation(
            summary = "List open auctions",
            description = "Retrieve all currently open auctions. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of open auctions retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/public")
    public List<AuctionDto> listOpen() {
        return service.getOpenAuctions();
    }


    @GetMapping("/by-item/{itemId}")
    public AuctionDto getActiveByItemId(@PathVariable Long itemId) {
        return service.getActiveAuctionByItemId(itemId);
    }

    // Watch l'enchère
    @PostMapping("/{id}/watch")
    public AuctionDto watch(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.watchAuction(id, userId);  // ✅ méthode avec userId
    }

    // Vérifier si déjà suivi
    @GetMapping("/{id}/is-watching")
    public boolean isWatching(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.isWatching(id, userId);
    }

    @GetMapping("/public/by-item/{itemId}")
    public AuctionDto getAuctionPublic(@PathVariable Long itemId) {
        return service.getAuctionPublic(itemId);
    }

    @GetMapping("/{id}/watchers")
    public List<UUID> getWatchers(@PathVariable Long id){
        return service.getWatcherIds(id);
    }

    @PostMapping("/{id}/close")
    public void close(@PathVariable Long id){
        service.closeAuction(id);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAuction(
            @PathVariable Long id
    ) {

        service.cancelAuction(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAuction(
            @PathVariable Long id,
            @RequestBody UpdateAuctionRequest dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        service.updateAuction(id, userId, dto);

        return ResponseEntity.ok().build();
    }
}