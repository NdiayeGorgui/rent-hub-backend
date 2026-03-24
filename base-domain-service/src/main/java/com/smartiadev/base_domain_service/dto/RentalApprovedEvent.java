package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record RentalApprovedEvent(
        Long rentalId,
        Long itemId,
        UUID ownerId,
        UUID renterId
) {}
