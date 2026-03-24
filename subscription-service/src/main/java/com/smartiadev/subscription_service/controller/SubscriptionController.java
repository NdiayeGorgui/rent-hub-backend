package com.smartiadev.subscription_service.controller;

import com.smartiadev.subscription_service.dto.PremiumStatusResponse;
import com.smartiadev.subscription_service.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Endpoints for managing user premium subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService service;


    /* ============================
       CHECK MY PREMIUM STATUS
       ============================ */
    @Operation(
            summary = "Check my premium status",
            description = "Returns true if the authenticated user has an active premium subscription."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Premium status retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
  /*  @GetMapping("/me")
    public boolean isMyAccountPremium(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.isPremium(userId);
    }*/

    @GetMapping("/me")
    public PremiumStatusResponse getMyPremiumStatus(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.getPremiumStatus(userId);
    }

    /* ============================
       SUBSCRIBE TO PREMIUM
       ============================ */
  /*  @Operation(
            summary = "Subscribe to premium",
            description = "Activate premium subscription for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription activated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "409", description = "User already subscribed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/subscribe")
    public void subscribe(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.subscribe(userId);
    }*/

    /* ============================
       CANCEL SUBSCRIPTION
       ============================ */
    @Operation(
            summary = "Cancel premium subscription",
            description = "Cancel the premium subscription of the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Active subscription not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/cancel")
    public void cancel(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.cancel(userId);
    }

    /* ============================
       INTERNAL CHECK (MICROSERVICES)
       ============================ */
    @Operation(
            summary = "Check premium status (internal)",
            description = "Internal endpoint used by other microservices (auth, auction, item) to verify if a user is premium."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Premium status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/internal/{userId}/is-premium")
    public boolean isPremiumInternal(
            @PathVariable UUID userId
    ) {
        return service.isPremium(userId);
    }

    @GetMapping("/internal/{userId}/status")
    public PremiumStatusResponse getPremiumStatusInternal(
            @PathVariable UUID userId
    ) {
        return service.getPremiumStatus(userId);
    }
  /*  @Operation(
            summary = "Activate premium subscription (internal)",
            description = "Internal endpoint used by payment-service to activate a premium subscription for a specific user after successful payment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Premium subscription activated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User already subscribed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/internal/subscribe/{userId}")
    public void subscribeInternal(
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "Unique identifier of the user",
                    required = true,
                    example = "3835c84b-d759-4bc7-a3ac-c3bc6bf81661"
            )
            @PathVariable UUID userId
    ) {
        service.subscribe(userId);
    }*/

}