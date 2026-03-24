package com.smartiadev.dispute_service.dto;

import java.util.UUID;

public record RentalInfoDTO(
        Long id,
        Long itemId,
        UUID ownerId,
        UUID renterId,
        String status
) {}

