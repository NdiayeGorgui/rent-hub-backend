package com.smartiadev.dispute_service.dto;

import java.util.UUID;

public record DisputeDto(
        Long id,
        Long rentalId,
        Long auctionId,
        Long itemId,
        UUID openedBy,
        String openedUsername,
        UUID reportedUserId,
        String reportedUsername,
        String itemTitle,
        String reason,
        String status,
        String adminDecision
) {}


