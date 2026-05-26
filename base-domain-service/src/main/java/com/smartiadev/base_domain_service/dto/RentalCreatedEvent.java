package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record RentalCreatedEvent(
        Long rentalId,
        Long itemId,
        UUID ownerId,
        UUID renterId
) {}