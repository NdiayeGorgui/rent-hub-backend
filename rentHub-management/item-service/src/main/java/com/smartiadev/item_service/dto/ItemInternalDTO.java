package com.smartiadev.item_service.dto;


import com.smartiadev.item_service.entity.ItemStatus;

import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        String title,
        UUID ownerId,
        boolean active,
        String type,
        ItemStatus status,
        Double pricePerDay
) {}

