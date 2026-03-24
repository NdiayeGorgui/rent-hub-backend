package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record DisputeCreatedEvent(
        Long disputeId,
        Long rentalId,
        Long itemId,
        UUID adminId,
        UUID openedBy,
        UUID reportedUserId,
        String reason

) {}