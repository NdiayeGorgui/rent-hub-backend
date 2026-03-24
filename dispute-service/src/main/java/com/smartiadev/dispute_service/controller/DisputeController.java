package com.smartiadev.dispute_service.controller;

import com.smartiadev.dispute_service.dto.CreateDisputeRequest;
import com.smartiadev.dispute_service.dto.DisputeDto;
import com.smartiadev.dispute_service.service.DisputeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
@Tag(name = "Disputes", description = "Endpoints for managing user disputes")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class DisputeController {

    private final DisputeService service;

    @Operation(
            summary = "Create a dispute",
            description = "Allows an authenticated user to create a new dispute related to a transaction, "
                    + "service, or other platform activity."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispute successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public DisputeDto create(
            @RequestBody(
                    description = "Dispute creation request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateDisputeRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody CreateDisputeRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.create(request, userId);
    }

    @Operation(
            summary = "Get my disputes",
            description = "Retrieves all disputes created by the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disputes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/my")
    public List<DisputeDto> my(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt) {

        return service.myDisputes(UUID.fromString(jwt.getSubject()));
    }
}