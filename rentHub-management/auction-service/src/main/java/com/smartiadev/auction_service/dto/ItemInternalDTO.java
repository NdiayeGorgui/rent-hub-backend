package com.smartiadev.auction_service.dto;


import com.smartiadev.auction_service.entity.ItemStatus;
import com.smartiadev.auction_service.entity.ItemType;

import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        UUID ownerId,
        Boolean active,
        ItemType type ,
        ItemStatus status,
        Double pricePerDay
) {}
