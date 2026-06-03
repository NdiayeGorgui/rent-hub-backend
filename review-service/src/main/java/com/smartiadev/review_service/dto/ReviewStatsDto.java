package com.smartiadev.review_service.dto;

public record ReviewStatsDto(
        Long totalReviews,
        Double averageRating
) {}
