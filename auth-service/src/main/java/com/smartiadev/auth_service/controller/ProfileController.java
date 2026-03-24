package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.UserProfileDto;
import com.smartiadev.auth_service.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for retrieving user profile information")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 🔐 PRIVATE PROFILE (Authenticated user)
     */
    @Operation(
            summary = "Get my profile",
            description = "Returns the profile information of the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "User profile not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public UserProfileDto getMyProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return profileService.getMyProfile(userId);
    }

    /**
     * 👤 PUBLIC PROFILE
     */
    @Operation(
            summary = "Get public user profile",
            description = "Returns the public profile information of a user by their unique identifier. "
                    + "This endpoint does not require authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User profile not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public UserProfileDto getPublicProfile(@PathVariable UUID userId) {
        return profileService.getPublicProfile(userId);
    }



}


