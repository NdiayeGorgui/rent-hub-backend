package com.smartiadev.auth_service.dto;

import java.util.UUID;

public record UserReviewStatsDto(

        Double averageRating,
        Long reviewsCount
) {}
