package com.smartiadev.dispute_service.dto;

import java.util.UUID;

public record DisputeDto(
        Long id,
        Long rentalId,
        Long itemId,
        UUID openedBy,
        UUID reportedUserId,
        String reason,
        String status,
        String adminDecision
) {}


