package com.smartiadev.rental_service.dto;

import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        String title,
        UUID ownerId,
        Double pricePerDay,
        Boolean active
) {}

