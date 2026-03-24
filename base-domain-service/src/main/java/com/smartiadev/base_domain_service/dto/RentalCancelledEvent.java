package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record RentalCancelledEvent(
        Long rentalId,
        Long itemId,
        UUID renterId,
        String reason
) {}

