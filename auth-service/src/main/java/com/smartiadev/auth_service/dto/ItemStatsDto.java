package com.smartiadev.auth_service.dto;

public record ItemStatsDto(
        Long totalItems,
        Long publishedItems,
        Long inactiveItems,
        Long newItems
) {}
