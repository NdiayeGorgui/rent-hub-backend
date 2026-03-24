package com.smartiadev.item_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ItemSearchRequest {

    private String city;
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private UUID ownerId;
    private LocalDateTime createdAfter;

    // pagination
    private int page = 0;
    private int size = 10;

    // tri
    private String sortBy = "createdAt";
    private String direction = "desc";
}
