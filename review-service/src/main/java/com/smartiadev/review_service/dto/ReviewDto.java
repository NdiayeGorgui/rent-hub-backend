package com.smartiadev.review_service.dto;

import java.util.UUID;

public record ReviewDto(
        Long id,
        Long itemId,
        UUID reviewerId,
        UUID reviewedUserId,
        String reviewerUsername,
        Integer rating,
        String comment
) {}
