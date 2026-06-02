package com.smartiadev.base_domain_service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RentalCreatedEvent(
        Long rentalId,
        Long itemId,
        UUID ownerId,
        UUID renterId,
        String renterUsername,
        String itemTitle,
        LocalDate startDate,
        LocalDate endDate
) {}