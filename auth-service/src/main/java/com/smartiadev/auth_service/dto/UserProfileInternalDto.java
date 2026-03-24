package com.smartiadev.auth_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserProfileInternalDto {

    private UUID userId;
    private String username;
    private String fullName;
    private String city;

    private Double averageRating;
    private Long reviewsCount;
    private String badge;
}

