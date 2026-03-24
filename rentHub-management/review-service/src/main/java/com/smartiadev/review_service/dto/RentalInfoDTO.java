package com.smartiadev.review_service.dto;

import java.util.UUID;

public record RentalInfoDTO(
        Long rentalId,
        Long itemId,
        UUID ownerId,
        UUID renterId,
        String status
) {}

