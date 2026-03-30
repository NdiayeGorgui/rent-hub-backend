package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.entity.ItemType;
import com.smartiadev.item_service.exception.ItemNotFoundException;
import com.smartiadev.item_service.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Endpoints for managing and searching items")
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemService service;

    private final ObjectMapper objectMapper;

    /* =====================
       CREATE ITEM (JWT)
       ===================== */
    @Operation(
            summary = "Create a new item",
            description = "Authenticated users can create a new item. The item's owner is inferred from the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })

    @PostMapping
    public ResponseEntity<ItemResponseDTO> create(
            @RequestBody @Valid ItemRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        System.out.println("JWT SUBJECT = " + jwt.getSubject());

        return ResponseEntity.ok(service.create(dto, ownerId));
    }

    /* =====================
       LIST ACTIVE ITEMS
       ===================== */
    @Operation(summary = "List all active items", description = "Retrieve all items that are currently active/available.")
    @GetMapping
    public List<ItemResponseDTO> list() {
        return service.findAllActive();
    }

    /* =====================
       GET ITEM BY ID
       ===================== */
    @Operation(summary = "Get item by ID", description = "Retrieve an item's full details by its ID.")
    @GetMapping("/{id}")
    public ItemResponseDTO get(@PathVariable Long id) {
        return service.findById(id);
    }

    /* =====================
       GET MY ITEMS (JWT)
       ===================== */
    @Operation(summary = "Get my items", description = "Retrieve all items created by the authenticated user.")
    @GetMapping("/me")
    public List<ItemResponseDTO> myItems(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return service.findByOwner(ownerId);
    }
    /* =====================
          GET MY PUBLISHED ITEMS (JWT)
          ===================== */
    @Operation(summary = "Get my published items (paginated)", description = "Retrieve items created by the authenticated user with pagination.")
    @GetMapping("/me/published")
    public Page<ItemResponseDTO> myPublishedItems(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return service.myPublishedItems(ownerId, page, size);
    }


    /* =====================
       DEACTIVATE ITEM (OWNER ONLY)
       ===================== */
    @Operation(summary = "Deactivate an item", description = "Authenticated users can deactivate their own items. Deactivated items are no longer available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        service.deactivate(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        service.activate(id, userId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/internal/{id}/activate")
    public ResponseEntity<Void> activateInternal(
            @PathVariable Long id,
            @RequestHeader(value = "X-Internal-Call", required = false) String internal
    ) {

        if (!"true".equals(internal)) {
            throw new RuntimeException("Forbidden");
        }

        service.activateBySystem(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<Void> updateItem(
            @PathVariable Long id,
            @RequestBody UpdateItemRequest dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        service.updateItem(id, userId, dto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get internal item data", description = "Internal endpoint for other microservices to fetch item data.")
    @GetMapping("/internal/{id}")
    public ItemInternalDTO getInternalItem(@PathVariable Long id) throws ItemNotFoundException {
        ItemResponseDTO item = service.findById(id);

        Double pricePerDay = null;
        if (item.getType() == ItemType.RENTAL) {
            pricePerDay = item.getPricePerDay();
        }

        return new ItemInternalDTO(
                item.getId(),
                item.getTitle(),
                item.getOwnerId(),
                item.getActive(),
                item.getType().name(),
                item.getStatus(),
                pricePerDay
        );
    }

    /**
     * Recherche d'articles disponibles avec filtres + tri
     */
    @Operation(summary = "Search items", description = "Search for items using filters (city, category, price, rating, availability) with sorting and pagination.")
    @GetMapping("/search")
    public Page<ItemSearchResponseDto> searchItems(
            @RequestParam(required = false) String keyword,

            // 📍 LOCALISATION
            @RequestParam(required = false) String city,

            // 🗂️ CATÉGORIE
            @RequestParam(required = false) Long categoryId,

            // 💰 PRIX
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,

            // ⭐ NOTE MINIMALE
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String type,

            // 📅 DISPONIBILITÉ
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            // 🔽 TRI
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,

            // 📄 PAGINATION
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

       /* Sort sort = Sort.by(
                Sort.Direction.fromString(direction),
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);*/
        Sort primarySort = Sort.by(
                Sort.Direction.fromString(direction),
                sortBy
        );

        Sort stableSort = primarySort.and(
                Sort.by(Sort.Direction.DESC, "id")
        );

        Pageable pageable = PageRequest.of(page, size, stableSort);

        return service.searchItems(
                keyword,
                city,
                categoryId,
                minPrice,
                maxPrice,
                startDate,
                endDate,
                minRating,
                type,
                pageable
        );
    }
    @Operation(summary = "Get published items by user", description = "Retrieve all published items of a specific user by their ID.")
    @GetMapping("/user/{userId}/published")
    public List<ItemSummaryDto> getPublishedItems(
            @PathVariable UUID userId
    ) {
        return service.getPublishedItemsByUser(userId);
    }

    @Operation(summary = "Get detailed item info", description = "Retrieve detailed information about an item by ID.")
    @GetMapping("/{id}/details")
    public ItemDetailsDto getItemDetails(@PathVariable Long id) {
        return service.getItemDetails(id);
    }





    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponseDTO> createWithImages(
            @RequestPart("data") String data,
            @RequestPart("images") List<MultipartFile> images,
            @AuthenticationPrincipal Jwt jwt
    ) throws Exception {

        ItemRequestDTO dto = objectMapper.readValue(data, ItemRequestDTO.class);

        UUID ownerId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
                service.createWithImages(dto, images, ownerId)
        );
    }

    @PutMapping(value = "/item/{id}/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateItemWithImages(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "existingImages", required = false) String existingImages,
            @AuthenticationPrincipal Jwt jwt
    ) throws Exception {

        UpdateItemRequest dto = objectMapper.readValue(data, UpdateItemRequest.class);

        UUID userId = UUID.fromString(jwt.getSubject());

        service.updateItemWithImages(id, userId, dto, images, existingImages);

        return ResponseEntity.ok().build();
    }

    // ItemController.java
    @GetMapping("/nearby")
    public List<ItemSummaryWithDistanceDto> getNearbyItems(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm
    ) {
        return service.getNearbyItems(lat, lng, radiusKm);
    }
}
