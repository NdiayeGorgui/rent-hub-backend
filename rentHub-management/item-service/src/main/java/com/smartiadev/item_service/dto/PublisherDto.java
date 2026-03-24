package com.smartiadev.item_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PublisherDto {

    private UUID userId;
    private String username;
    private String fullName;
    private String city;

    private Double averageRating;
    private Long reviewsCount;
    private String badge;
}
