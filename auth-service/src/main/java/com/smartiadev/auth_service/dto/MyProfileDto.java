package com.smartiadev.auth_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class MyProfileDto {

    private UUID userId;
    private String username;
    private String fullName;
    private String city;

    private Boolean premium;

    private Double averageRating;
    private Long reviewsCount;

    private String badge;
    private List<String> roles;
}
