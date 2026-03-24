package com.smartiadev.auth_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserProfileDto {

    // IDENTITÉ
    private UUID userId;
    private String username;
    private String fullName;

    // LOCALISATION
    private String city;

    // ⭐ PREMIUM (INFO CALCULÉE)
    private boolean premium;

    // RÉPUTATION
    private Double averageRating;
    private Long reviewsCount;
    private String badge;

    // HISTORIQUE
    private List<ItemSummaryDto> publishedItems;
    private List<ItemSummaryDto> rentedItems;
    private List<String> roles;

    // PRIVÉ (plus tard)
    // private String email;
    // private String phone;
}

