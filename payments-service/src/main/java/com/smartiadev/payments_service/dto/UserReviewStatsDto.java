package com.smartiadev.payments_service.dto;

public record UserReviewStatsDto(
        Double averageRating,
        Long reviewsCount
) {}


//GET /internal/users/{userId}/stats  et qui retourne les deux d'un coup.