package com.smartiadev.item_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemSearchResponseDto {

    private Long id;
    private String title;
    private String description;
    private String city;
    private Double pricePerDay;
    private Double averageRating; // ⭐ NOUVEAU
    private String type;
    private String imageUrl;
}
