package com.smartiadev.item_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AdminItemDto {

    private Long itemId;
    private String title;
    private String description;

    private String city;
    private String address;

    private Double pricePerDay;
    private Boolean active;

    private String type;

    private List<String> imageUrls;

    // publisher
    private UUID userId;
    private String username;
    private String fullName;
    private String publisherCity;

    private Double averageRating;
    private Long reviewsCount;
    private String badge;

    // subscription
    private Boolean premium;
    private Boolean gracePeriod;

    // auction
    private Double currentPrice;
}