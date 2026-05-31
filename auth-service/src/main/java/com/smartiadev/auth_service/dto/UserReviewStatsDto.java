package com.smartiadev.auth_service.dto;

import java.util.UUID;

public record UserReviewStatsDto(
        UUID userId,
        Double averageRating,
        Long reviewsCount
) {}
