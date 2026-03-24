package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.dto.AdminItemDto;
import com.smartiadev.item_service.dto.ItemResponseDTO;
import com.smartiadev.item_service.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
@Tag(name = "Admin Items", description = "Administrative endpoints for managing items (create, activate, deactivate, list)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminItemController {

    private final ItemService itemService;

    /* =====================
       LIST ALL ITEMS (ADMIN)
       ===================== */
    @Operation(
            summary = "List all items",
            description = "Retrieve all items including inactive ones. Accessible only by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of items retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<ItemResponseDTO> listAll() {
        return itemService.findAllIncludingInactive();
    }

    /* =====================
       DEACTIVATE ITEM (ADMIN)
       ===================== */
    @Operation(
            summary = "Deactivate an item",
            description = "Deactivate an item by its ID. The item will no longer be visible or usable on the platform. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item successfully deactivated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Long id) {
        itemService.adminDeactivate(id);
    }

    /* =====================
       ACTIVATE ITEM (ADMIN)
       ===================== */

    @Operation(
            summary = "Activate an item",
            description = "Activate an item by its ID. The item becomes available again on the platform. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item successfully activated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Administrator role required"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/activate")
    public void activate(@PathVariable Long id) {
        itemService.adminActivate(id);
    }

    @GetMapping("/full")
    public List<AdminItemDto> listAllFull() {
        return itemService.findAllAdminItems();
    }
}

