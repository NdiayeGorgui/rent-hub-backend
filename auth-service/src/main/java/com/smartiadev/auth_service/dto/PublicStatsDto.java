package com.smartiadev.auth_service.dto;

public record PublicStatsDto(
        long activeItems,
        long openAuctions,
        long totalMembers,
        double averageRating
) {}
