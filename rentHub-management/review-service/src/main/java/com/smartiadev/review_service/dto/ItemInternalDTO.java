package com.smartiadev.review_service.dto;


import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        UUID ownerId,
        boolean active,
        String type,
        Double pricePerDay
) {}

