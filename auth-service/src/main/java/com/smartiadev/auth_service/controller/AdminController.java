package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.response.UserResponseDto;
import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Endpoints for administrators to manage platform users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "Get all users",
            description = "Retrieve the complete list of registered users. Accessible only by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<UserResponseDto> listUsers() {
        return adminService.getAllUsers();
    }

    @Operation(
            summary = "Suspend a user by admin",
            description = "Allows an administrator to suspend a user after a violation of the platform rules. "
                    + "The suspension can be temporary or permanent depending on the severity of the violation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully suspended"),
            @ApiResponse(responseCode = "400", description = "Invalid request (missing or incorrect data)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/suspend")
    public void suspend(@PathVariable UUID id) {
        adminService.suspendUser(id);
    }

    @Operation(
            summary = "Activate a suspended user",
            description = "Allows an administrator to reactivate a previously suspended user, restoring full access to the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully activated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/activate")
    public void activate(@PathVariable UUID id) {
        adminService.activateUser(id);
    }
}
