package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record ReviewCreatedEvent(
        Long reviewId,
        Long rentalId,
        Long itemId,
        UUID reviewerId,
        UUID reviewedUserId,
        Integer rating
) {}

