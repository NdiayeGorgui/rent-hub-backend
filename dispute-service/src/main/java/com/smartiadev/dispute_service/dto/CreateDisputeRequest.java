package com.smartiadev.dispute_service.dto;

import java.util.UUID;

public record CreateDisputeRequest(
        Long rentalId,        // null si litige enchère
        Long auctionId,       // null si litige location
        UUID reportedUserId,  // le winner qui refuse de payer
        String reason,
        String description
) {}

