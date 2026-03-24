package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.dto.ItemSearchRequest;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.service.ItemQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items/public")
@RequiredArgsConstructor
@Tag(name = "Public Items", description = "Endpoints for public search and listing of published items")
public class ItemPublicController {

    private final ItemQueryService itemQueryService;

    @Operation(
            summary = "Search published items",
            description = "Search for published items using various filters such as location, category, price, availability, and rating. Publicly accessible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/search")
    public Page<Item> searchPublishedItems(
            @RequestBody ItemSearchRequest request
    ) {
        return itemQueryService.searchPublishedItems(request);
    }
}
