package com.smartiadev.auth_service.dto;

public record ReviewStatsDto(
        Long totalReviews,
        Double averageRating
) {}
