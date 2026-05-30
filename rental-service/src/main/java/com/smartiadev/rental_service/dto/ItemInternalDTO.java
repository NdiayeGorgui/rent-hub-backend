package com.smartiadev.rental_service.dto;

import java.util.List;
import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        String title,
        UUID ownerId,
        Double pricePerDay,
        Boolean active,
        List<String> imageUrls
) {}

