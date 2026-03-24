package com.smartiadev.dispute_service.controller;

import com.smartiadev.dispute_service.dto.DisputeDto;
import com.smartiadev.dispute_service.dto.ResolveDisputeRequest;
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
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@Tag(name = "Admin Dispute Management", description = "Administrative endpoints for managing and resolving disputes")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisputeController {

    private final DisputeService service;

    @Operation(
            summary = "Get all disputes",
            description = "Retrieves all disputes in the system. Accessible only by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disputes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<DisputeDto> all() {
        return service.all();
    }

    @Operation(
            summary = "Resolve a dispute",
            description = "Allows an administrator to resolve a dispute by approving, rejecting, "
                    + "or applying a decision. The action is recorded with the administrator ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispute successfully resolved"),
            @ApiResponse(responseCode = "400", description = "Invalid resolution request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "404", description = "Dispute not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/resolve")
    public void resolve(
            @Parameter(description = "Unique identifier of the dispute", required = true)
            @PathVariable Long id,

            @RequestBody(
                    description = "Resolution details including decision and optional admin comment",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResolveDisputeRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ResolveDisputeRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID adminId = UUID.fromString(jwt.getSubject());
        service.resolve(id, request, adminId);
    }
}