package com.smartiadev.item_service.dto;

import java.util.UUID;

public record UserReviewStatsDto(

        UUID ownerId,
        Double averageRating,
        Long reviewsCount

) {}