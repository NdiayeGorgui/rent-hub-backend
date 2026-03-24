package com.smartiadev.item_service.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
        Long id,
        UUID reviewerId,
        UUID reviewedUserId,
        int rating,
        String comment,
        LocalDateTime createdAt
) {}
