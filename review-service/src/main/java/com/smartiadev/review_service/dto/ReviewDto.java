package com.smartiadev.review_service.dto;

import java.util.UUID;

public record ReviewDto(
        Long id,
        Long itemId,

        UUID reviewerId,
        UUID reviewedUserId,

        String reviewerUsername,

        Double reviewerAverageRating,
        Long reviewerReviewsCount,
        String reviewerBadge,

        Integer rating,
        String comment
) {}
